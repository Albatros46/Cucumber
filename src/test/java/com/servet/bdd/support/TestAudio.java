package com.servet.bdd.support;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** A valid, silent WAV file of 0.1 s: enough for the API to accept it as a song. */
public final class TestAudio {

    public static byte[] silentWav() {
        int samples = 800;
        ByteBuffer b = ByteBuffer.allocate(44 + samples * 2).order(ByteOrder.LITTLE_ENDIAN);
        b.put("RIFF".getBytes()).putInt(36 + samples * 2).put("WAVE".getBytes()).put("fmt ".getBytes())
                .putInt(16).putShort((short) 1).putShort((short) 1).putInt(8000).putInt(16000)
                .putShort((short) 2).putShort((short) 16).put("data".getBytes()).putInt(samples * 2);
        return b.array();
    }

    private TestAudio() {
    }
}
