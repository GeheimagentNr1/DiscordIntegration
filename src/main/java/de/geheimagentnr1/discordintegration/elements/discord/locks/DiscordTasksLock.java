package de.geheimagentnr1.discordintegration.elements.discord.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DiscordTasksLock extends ReentrantReadWriteLock {
	
	
	public void lockRead() {
		
		readLock().lock();
	}
	
	public void unlockRead() {
		
		readLock().unlock();
	}
	
	public void lockWrite() {
		
		writeLock().lock();
	}
	
	public void unlockWrite() {
		
		writeLock().unlock();
	}
}
