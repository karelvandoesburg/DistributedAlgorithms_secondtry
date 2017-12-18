package RandomizedByzantine;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Process extends UnicastRemoteObject implements ProcessIF{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private int processID;
	private int v;
	private int round;
	private String messagetype;
	private ArrayList<Message> receivedmessages;
	private String host = "rmi://localhost:1099";
	private int amountofprocesses;
	private int amountoffaultyprocesses;
	
	protected Process() throws RemoteException {
		super();
		this.v = Process.createRandomNumberBetween(0, 1);
		this.round = 1;
		this.messagetype = "N";
		this.receivedmessages = new ArrayList<Message>();
	}
	
	
	
	@Override
	public void broadcastN() {
		System.out.println(this.createMessage());
		for(int i = 0; i < this.amountofprocesses; i++) {
			if(i != this.processID) {this.createAndSendMessage(i);}
		}
	}
	
	public void createAndSendMessage(int i) {
		try {
			ProcessIF process = Main.getProcess(host, i);
			Message message = createMessage();
			process.receiveMessage(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message createMessage() {
		return new Message(this.messagetype, this.round, this.v);
	}
	
	@Override
	public void receiveMessage(Message message) {
		this.receivedmessages.add(message);
	}
	
	
	
	
	@Override
	public void processN() throws RemoteException {
		int amountofmessagesneeded = this.amountofprocesses/2;
		int valueforP = this.selectNewPValue(amountofmessagesneeded, this.messagetype, this.round);
		this.messagetype = "P";
		this.broadcastP(valueforP);
	}
	
	public int selectNewPValue(int amount, String type, int round) {
		int value0messages = 0;
		int value1messages = 0;
		for(Message message : this.receivedmessages) {
			if(this.compareMessageTypeAndRound(message, type, round)) {
				if(message.getMessageValue() == 0) {value0messages++;}
				else {value1messages++;}
			}
		}
		return this.choosePvalue(amount, value0messages, value1messages);
	}
	
	public boolean compareMessageTypeAndRound(Message message, String type, int round) {
		return ((message.getMessageType().equals(type)) && (message.getMessageRound() == round));
	}
	
	public int choosePvalue(int amount, int value0messages, int value1messages) {
		if (value0messages > amount) 		{return 0;}
		else if (value1messages > amount) 	{return 1;}
		else 								{return Integer.MIN_VALUE;}
	}
	
	public void broadcastP(int w) {
		System.out.println(this.createMessage(w));
		for(int i = 0; i < this.amountofprocesses; i++) {
			if(i != this.processID) {this.createAndSendMessage(i, w);}
		}
	}
	
	public void createAndSendMessage(int i, int value) {
		try {
			ProcessIF process = Main.getProcess(host, i);
			Message message = createMessage(value);
			process.receiveMessage(message);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Message createMessage(int value) {
		return new Message(this.messagetype, this.round, value);
	}
	
	
	

	@Override
	public void decide() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processP() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void runRound() {
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void awaitMessages() {
		Boolean receivedenoughmessages = false;
		while(!receivedenoughmessages) {
			receivedenoughmessages = this.checkIfEnoughMessagesAreReceived(this.messagetype, this.round);
		}
		System.out.println("process " + this.processID + " has received its messages");
	}
	
	public synchronized Boolean checkIfEnoughMessagesAreReceived(String type, int round) {
		int amountofmessagesthatshouldbereceived = this.amountofprocesses - this.amountoffaultyprocesses;
		int amountofmessagesreceived = 0;
		for(Message message : this.receivedmessages) {
			if(this.compareMessageTypeAndRound(message, type, round)) {
				amountofmessagesreceived++;
				if(amountofmessagesreceived == amountofmessagesthatshouldbereceived) {return true;}
			}
		}
		return false;
	}
	
	
	public void setProcessID(int ID) {
		this.processID = ID;
	}
	
	public int getProcessID() {
		return this.processID;
	}
	
	public static int createRandomNumberBetween(int smallest, int largest) {
		return (int )(Math.random() * ((largest+1)-smallest) + smallest);
	}

	@Override
	public void setAmountOfProcesses(int amountofprocesses) throws RemoteException {
		this.amountofprocesses = amountofprocesses;
	}
	
	@Override
	public void setAmountOfFaultyProcesses(int amountoffaultyprocesses) throws RemoteException {
		this.amountoffaultyprocesses = amountoffaultyprocesses;
	}
	
}
