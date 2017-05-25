package io.github.leovr.rtipmidi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import lombok.Setter;

@Setter
public class BidirectionalMidiSession extends JavaxAppleMidiSession implements Transmitter, Receiver {
	@Nonnull
	private AppleMidiServer server;
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
			server.sendMidiMessage(Arrays.asList(msg));
		} catch (Exception e) {
		}
	}

	public void send(List<MidiMessage> list) {
		List<io.github.leovr.rtipmidi.model.MidiMessage> l1 = list.stream().map(o -> new io.github.leovr.rtipmidi.model.MidiMessage(o.getMessage(), o.getLength()) {
		}).collect(Collectors.toList());
		try {
			server.sendMidiMessage(l1);
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
