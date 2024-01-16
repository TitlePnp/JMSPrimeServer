/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmsprimeserver;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author jiapat
 */
public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(mappedName = "jms/SimpleJMSQueue")
    private static Queue simpleJMSQueue;
    
    public static void main(String[] args) {
        Connection connection = null;
        Session session = null;
        TextListener listener = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(simpleJMSQueue);
            listener = new TextListener(session, producer);
            consumer = session.createConsumer(simpleJMSQueue);
            consumer.setMessageListener(listener);
            TextMessage message = session.createTextMessage();
            
            connection.start();
            
            Scanner sc = new Scanner(System.in);
            while(true) {
                System.out.println("Press q to quit ");
                String input = sc.nextLine();
                if (input.equals("q")) {
                    break;
                }
            }
            
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}