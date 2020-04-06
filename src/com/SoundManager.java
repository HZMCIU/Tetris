package com;

import java.io.*;

import javazoom.jl.player.advanced.*;
import javazoom.jl.decoder.*;
import javazoom.jl.player.*;
import javazoom.jl.player.advanced.PlaybackEvent;

public class SoundManager implements Runnable {
	private Bitstream bitstream;
	/** The MPEG audio decoder. */
	private Decoder decoder;
	/** The AudioDevice the audio samples are written to. */
	private AudioDevice audio;
	/** Has the player played back all frames from the stream? */
	private boolean notCompleted = false;
	private int framesInACircle = 10;
	private Thread t;
	private Clock logicTimer;
	private String musicPath;

	public SoundManager(InputStream stream) throws JavaLayerException {
		this(stream, null);
	}

	public SoundManager(InputStream stream, AudioDevice device) throws JavaLayerException {
		 bitstream = new Bitstream(stream);

		if (device != null)
			audio = device;
		else
			audio = FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder = new Decoder());
	}

	public SoundManager(String musicPath, Clock logicTimer) throws JavaLayerException, FileNotFoundException {
		this(new BufferedInputStream(new FileInputStream(musicPath)));
		this.logicTimer = logicTimer;
		this.musicPath = musicPath;
	}

	public synchronized void close() {
		AudioDevice out = audio;
		if (out != null) {
			audio = null;
			// this may fail, so ensure object state is set up before
			// calling this method.
			out.close();
			try {
				bitstream.close();
			} catch (BitstreamException ex) {
			}
		}
	}
	public synchronized void reOpenStream() throws FileNotFoundException, JavaLayerException {
		bitstream = new Bitstream(new FileInputStream(musicPath));
		audio = FactoryRegistry.systemRegistry().createAudioDevice();
		audio.open(decoder = new Decoder());
	}
	protected boolean decodeFrame() throws JavaLayerException {
		try {
			AudioDevice out = audio;
			if (out == null)
				return false;

			Header h = bitstream.readFrame();
			if (h == null)
				return false;

			// sample buffer set when decoder constructed
			SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

			synchronized (this) {
				out = audio;
				if (out != null) {
					out.write(output.getBuffer(), 0, output.getBufferLength());
				}
			}

			bitstream.closeFrame();
		} catch (RuntimeException ex) {
			throw new JavaLayerException("Exception decoding audio frame", ex);
		}
		return true;
	}

	public void run() {
		notCompleted=true;
		while (true) {
			framesInACircle = 10;
			synchronized (this) {
				while (framesInACircle >= 0 && logicTimer.isPaused == false && notCompleted == true) {
					try {
						notCompleted = decodeFrame();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
				}
			}
			if (notCompleted == false) {
				try {
					if (bitstream != null) {
						close();
						bitstream=null;
					}
					if (bitstream == null) {
						reOpenStream();
					}
					notCompleted = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		if (t == null) {
			t = new Thread(this);
		}
		t.start();
	}
}
