/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002-2018 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.betanzos.jsch;

import java.io.InputStream;

public class ChannelWritableShell extends ChannelSession {

    private Buffer buf;
    private Packet packet;
    private byte[] data;
    private int dataIndex = 0;

    ChannelWritableShell() {
        super();
        pty = true;
    }

    @Override
    public void setInputStream(InputStream in) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInputStream(InputStream in, boolean dontClose) {
        throw new UnsupportedOperationException();
    }

    public void start() throws JSchException {
        Session _session = getSession();
        try {
            sendRequests();

            Request request = new RequestShell();
            request.request(_session, this);
        } catch (Exception e) {
            if(e instanceof JSchException) throw (JSchException) e;
            if(e instanceof Throwable) throw new JSchException("ChannelWritableShell", (Throwable)e);
            throw new JSchException("ChannelWritableShell");
        }

        this.buf = new Buffer(rmpsize);
        this.buf.reset();
        this.packet = new Packet(buf);
    }

    void init() throws JSchException {
        io.setInputStream(getSession().in);
        io.setOutputStream(getSession().out);
    }

    public synchronized void send(byte[] data) throws JSchException {
        if (close) {
            throw new JSchException("ChannelWritableShell is closed");
        }

        if (data != null && data.length > 0) {
            this.data = data;

            int copied = copyDataTo(buf.buffer, 14, buf.buffer.length -14 - Session.buffer_margin);

            while (copied > 0) {
                packet.reset();
                buf.putByte((byte) Session.SSH_MSG_CHANNEL_DATA);
                buf.putInt(recipient);
                buf.putInt(copied);
                buf.skip(copied);
                try {
                    getSession().write(packet, this, copied);
                } catch (Exception e) {
                    throw new JSchException("Error sending data", e);
                }

                copied = copyDataTo(buf.buffer, 14, buf.buffer.length -14 - Session.buffer_margin);
            }
        }
    }

    @Override
    public void run() {

    }

    private int copyDataTo(byte[] dest, int offset, int length) {
        int copied = 0;

        if (data != null) {
            while (dataIndex < data.length && copied < length) {
                dest[offset++] = data[dataIndex++];
                copied++;
            }

            if (dataIndex == data.length) {
                data = null;
                dataIndex = 0;
            }
        }

        return copied;
    }
}
