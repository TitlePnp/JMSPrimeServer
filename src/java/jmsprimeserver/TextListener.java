/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsprimeserver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author jiapat
 */
public class TextListener implements MessageListener {
    private MessageProducer producer;
    private Session session = null;
    
    public TextListener(Session session, MessageProducer producer) {
        this.session = session;
        this.producer = producer;
    }
    
    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            
            Queue temporaryDest = session.createTemporaryQueue();
            TextMessage response = session.createTextMessage();
            
            String[] primeRange = msg.getText().split(",");
            int startNum = Integer.parseInt(primeRange[0]);
            int endNum = Integer.parseInt(primeRange[1]);
            int primesSet = primesSetCount(startNum, endNum);
            response.setText("The number of primes between " + startNum + " and " + endNum
                            + " is " + primesSet);
            response.setJMSReplyTo(temporaryDest);
            System.out.println("Sending message: " + response.getText());
            producer.send(response);
            
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
    }
    
    private boolean isPrime(int n) {
        for (int i = 2; i*i <= n; i++) {
            if ((n % i) == 0) {
                return false;
            }
        }
        return true;
    }
    
    private int primesSetCount(int startNum, int endNum) {
        int primesCnt = 0;
        for(int i = startNum; i <= endNum; i++) {
            if(isPrime(i)) {
                primesCnt += 1;
            }
        }
        return primesCnt;
    }
}