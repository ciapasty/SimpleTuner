package hardware;

import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HardwareConnector {

    private final static int INT24MAX_VALUE = 8388607;

    private int bufferSize; // in samples
    private AudioFormat audioFormat;

    private TargetDataLine targetDataLine;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    private Thread captureThread;
    private boolean stopCapture = false;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int samples) {
        if (samples <= 0) throw new IllegalArgumentException(
                "Buffer size is less or equal 0");
        this.bufferSize = samples;
        // TODO: Reinitialize pipes
    }

    public void initialize(AudioFormat audioFormat, int bufferSize)
            throws IOException
    {
        if (bufferSize <= 0) throw new IllegalArgumentException(
                "Buffer size is less or equal 0");
        this.audioFormat = audioFormat;
        this.bufferSize = bufferSize;

        pipedOutputStream = new PipedOutputStream();
        pipedInputStream = new PipedInputStream(
                pipedOutputStream, getBufferSizeInBytes());
    }

    public void useDefaultAudioDevice()
            throws LineUnavailableException
    {
        DataLine.Info info = new DataLine.Info(
                TargetDataLine.class, audioFormat);
        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Audio format not supported.");
        }
        // Obtain and open the targetDataLine.
        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);

        // TODO: Rethink if necessary
        targetDataLine.addLineListener(
                event -> {
                        if (event.getType() == LineEvent.Type.START) {
                            System.out.println("Start event");
                        } else if (event.getType() == LineEvent.Type.STOP) {
                            System.out.println("Stop event");
                        } else if (event.getType() == LineEvent.Type.OPEN) {
                            System.out.println("Open event");
                        } else if (event.getType() == LineEvent.Type.CLOSE) {
                            System.out.println("Close event");
                        }
                }
        );

        targetDataLine.open(audioFormat, getBufferSizeInBytes());
        // TODO: change to logger
        System.out.println("Line open: " + targetDataLine.isOpen() +
                "; Buffer size: " + targetDataLine.getBufferSize());
    }

    public void startAudioCapture() {
        stopCapture = false;
        targetDataLine.flush();
        targetDataLine.start();
        captureThread = new Thread(new CaptureThread());
        captureThread.start();
    }

    public void stopAudioCapture() {
        if (captureThread == null || stopCapture) {
            return;
        }
        captureThread.interrupt();
    }

    public float[] getAudioBuffer() throws IOException {
        float[] out = new float[bufferSize];

        int bytesRead;
        int bytesPerSample = audioFormat.getSampleSizeInBits() / 8;
        byte[] buffer = new byte[bytesPerSample];
        ByteBuffer bb;
        for (int i = 0; i < bufferSize; i++) {
            bytesRead = pipedInputStream.read(buffer);
            if (bytesRead <= 0) {
                throw new IOException("0 bytes read from audio buffer");
            }

            bb = ByteBuffer.wrap(buffer);
            switch (bytesPerSample) {
                case 1:
                    out[i] = (float)((short)(bb.get() & 0xff) - Byte.MAX_VALUE)/Byte.MAX_VALUE;
                    break;
                case 2:
                    out[i] = (float)bb.getShort()/Short.MAX_VALUE;
                    break;
                case 3:
                    out[i] = (float) intFrom3Bytes(buffer, ByteOrder.BIG_ENDIAN) / INT24MAX_VALUE;
                    break;
                default:
                    break;
            }
        }

        return out;
    }

    private int getBufferSizeInBytes() {
        return bufferSize * audioFormat.getSampleSizeInBits() / 8;
    }

    private int intFrom3Bytes(byte[] bytes, ByteOrder byteOrder) {
        if(byteOrder == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(bytes);
        }
        int value = (((bytes[0] & 0xFF) << 16) | ((bytes[1] & 0xFF) <<  8) | (bytes[2] & 0xFF));
        // 24 bit signed integer fix
        if ((value & 0x00800000) > 0) {
            return value | 0xFF000000;
        } else {
            return value & 0x00FFFFFF;
        }
    }

    // Capture Thread class

    private class CaptureThread extends Thread {
        // temporary buffer
        byte[] buffer = new byte[getBufferSizeInBytes()];

        public void run() {
            try {
                while (!isInterrupted()) {
                    int cnt = targetDataLine.read(
                            buffer, 0, buffer.length);
                    if (cnt > 0) {
                        pipedOutputStream.write(buffer, 0, cnt);
                    }
                }
                pipedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
