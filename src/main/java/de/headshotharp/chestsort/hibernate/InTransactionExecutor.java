package de.headshotharp.chestsort.hibernate;

import org.hibernate.Session;

@FunctionalInterface
public interface InTransactionExecutor<T> {
	public T executeInTransaction(Session session);
}
