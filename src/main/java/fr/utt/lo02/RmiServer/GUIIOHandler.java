package fr.utt.lo02.RmiServer;

import fr.utt.lo02.IO.IOHandler;

public interface GUIIOHandler extends IOHandler {
    public String getIp();
    public String getUserName();
}
