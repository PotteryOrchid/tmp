package com.dolphin.api.test;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.net.ConnectException;

public class Test {

  public static boolean execCmd(String hostname, String username, String password, String cmd) {
    boolean flag = false;
    try {
      Connection conn = new Connection(hostname);
      conn.connect();
      boolean isAuthenticated = conn.authenticateWithPassword(username, password);

      if (isAuthenticated == false) {
        throw new IOException("Authentication failed.");
      }

      Session sess = conn.openSession();
      sess.execCommand(cmd);

      InputStream stdout = new StreamGobbler(sess.getStdout());
      BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
      while (true) {
        String line = br.readLine();
        if (line == null) {
          break;
        }
        System.out.println(line);
      }
      sleep(500);
      if (sess.getExitStatus() != null && 0 == sess.getExitStatus()) {
        flag = true;
      }
      br.close();
      sess.close();
      conn.close();
    } catch (ConnectException e) {
      if ("Connection timed out: connect".equals(e.getMessage())) {
        e.printStackTrace();
        System.out.println("Please check network connect or input network info.");
        System.exit(2);
      } else if ("Connection refused: connect".equals(e.getMessage())) {
        e.printStackTrace();
        System.out.println("Please check network connect config info.");
        System.exit(2);
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
      System.exit(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(2);
    }
    return flag;
  }

  public static void main(String[] args) {
    String hostname = "192.168.125.131";
    String username = "zj";
    String password = "ubuntu";
    String cmd = "echo ubuntu | sudo -S service docker restart";
    System.out.println(execCmd(hostname, username, password, cmd));
  }
}
