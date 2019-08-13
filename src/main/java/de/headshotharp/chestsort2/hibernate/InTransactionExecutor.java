package de.headshotharp.chestsort2.hibernate;

import org.hibernate.Session;

@FunctionalInterface
public interface InTransactionExecutor<T> {
	public T executeInTransaction(Session session);
}
