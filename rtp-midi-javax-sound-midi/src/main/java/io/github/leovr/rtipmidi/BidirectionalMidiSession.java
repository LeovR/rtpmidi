package io.github.leovr.rtipmidi;

import java.net.InetAddress;
import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import lombok.Setter;

@Setter
public class BidirectionalMidiSession extends JavaxAppleMidiSession implements Transmitter, Receiver {
	@Nonnull
	private AppleMidiServer server;
	@Nonnull
	private InetAddress inetAddress;
	private int port;

	private Receiver receiver;

	@Override
	protected void onMidiMessage(MidiMessage message, long timestamp) {
		if (receiver != null) {
			receiver.send(message, timestamp);
		}
	}

	@Override
	public void send(MidiMessage message, long timeStamp) {
		io.github.leovr.rtipmidi.model.MidiMessage msg = new io.github.leovr.rtipmidi.model.MidiMessage(message.getMessage(), message.getLength()) {
		};
		try {
			io.github.leovr.rtipmidi.model.AppleMidiServer s = null;
			if(inetAddress!=null&&port>0){
				s = new io.github.leovr.rtipmidi.model.AppleMidiServer(inetAddress, port);
			}
			server.sendMidiMessage(Arrays.asList(msg), s);
		} catch (Exception e) {
		}
	}

	@Override
	public Receiver getReceiver() {
		return this;
	}

	@Override
	public void close() {

	}

}
