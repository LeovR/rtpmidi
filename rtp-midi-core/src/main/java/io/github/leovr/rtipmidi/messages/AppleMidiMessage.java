package io.github.leovr.rtipmidi.messages;
import java.nio.ByteBuffer;
import java.util.List;

import io.github.leovr.rtipmidi.model.MidiMessage;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class AppleMidiMessage {

	private final MidiCommandHeader midiCommandHeader;
	private final List<MidiMessage> messages;

	public byte[] getBytes(){
		ByteBuffer bb = ByteBuffer.wrap(new byte[1024]);
		RtpHeader rtpHeader = midiCommandHeader.getRtpHeader();
		byte header1 = 0, header2 = 0;
		header1 |= rtpHeader.getVersion() << 6;
		if (rtpHeader.isPaddingFlag()) {
			header1 |= 1 << 5;
		}
		if (rtpHeader.isExtensionFlag()) {
			header1 |= 1 << 4;
		}
		header1 |= rtpHeader.getContributingSourceIdentifiersCount() & 0x0F;
		if (rtpHeader.isMarkerFlag()) {
			header2 |= 1 << 7;
		}
		header2 |= 0x61;

		bb.put(header1);
		bb.put(header2);
		bb.putShort(rtpHeader.getSequenceNumber());
		bb.putInt(rtpHeader.getTimestamp());
		bb.putInt(rtpHeader.getSsrc());
		int mark_pos1 = bb.position();
		byte midiCommandHeader1 = 0, midiCommandHeader2 = 0;
		midiCommandHeader1 |= 1 << 7;
		if (midiCommandHeader.isJ()) {
			midiCommandHeader1 |= 1 << 6;
		}
		if (midiCommandHeader.isP()) {
			midiCommandHeader1 |= 1 << 4;
		}
		bb.put(midiCommandHeader1);
		bb.put(midiCommandHeader2);
		int mark_pos2 = bb.position();

		for (MidiMessage msg : messages) {
			if (msg == null) {
				continue;
			}
			bb.put(msg.getData(), 0, msg.getLength());
		}
		byte[] arr = new byte[bb.position()];
		bb.position(0);
		bb.get(arr);
		int length = arr.length - mark_pos2;
		midiCommandHeader2 = (byte) (length & 0xFF);
		midiCommandHeader1 |= (byte) ((length & 0x0F00) >> 8);
		arr[mark_pos1] = midiCommandHeader1;
		arr[mark_pos1 + 1] = midiCommandHeader2;
		return arr;
	}
}
