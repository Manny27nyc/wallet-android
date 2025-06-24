/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package nordpol;

import java.io.IOException;
import java.util.List;

public interface IsoCard {
    public void addOnCardErrorListener(OnCardErrorListener listener);
    public void removeOnCardErrorListener(OnCardErrorListener listener);
    public void close() throws IOException;
    public void connect() throws IOException;
    public int getMaxTransceiveLength() throws IOException;
    public int getTimeout();
    public boolean isConnected();
    public void setTimeout(int timeout);
    public byte[] transceive(byte[] data) throws IOException;
    public List<byte[]> transceive(List<byte[]> data) throws IOException;
}
