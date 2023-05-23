package core.database.access;

import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
@Log4j2
public abstract class Dao<T, I>
{
    
    private final SessionFactory sessionFactory;
    private final String tableName;
    private final Class<T> clazz;
    
    public Dao(SessionFactory sessionFactory, Class<T> clazz, String tableName)
    {
        this.sessionFactory = sessionFactory;
        this.tableName = tableName;
        this.clazz = clazz;
    }
    
    public synchronized Optional<T> get(I identifier)
    {
        Session session = getSession();
        return Optional.ofNullable(session.find(clazz, identifier));

    }
    
    public synchronized List<T> getAll()
    {
        Session session = getSession();
        List<T> all =  createQuery(session, "").getResultList();
        session.close();
        return  all;
    }
    
    public synchronized void save(T object)
    {
        executeInsideTransaction(session -> session.persist(object));
    }
    
    public synchronized void update(T object)
    {
        executeInsideTransaction(session -> session.merge(object));
    }
    
    public synchronized void saveOrUpdate(T object)
    {
        executeInsideTransaction(session -> session.saveOrUpdate(object));
    }
    
    public synchronized void delete(T object)
    {
        executeInsideTransaction(session -> session.remove(object));
    }
    
    protected synchronized Query<T> createQuery(Session session, String queryCommand)
    {
        String queryStr = "from " + tableName + (queryCommand.isEmpty() ? "" : " " + queryCommand);
        return session.createQuery(queryStr, clazz);
    }
    
    protected synchronized void executeInsideTransaction(Consumer<Session> action)
    {
        Session session = getSession();
        EntityTransaction transaction = session.getTransaction();
        try
        {
            transaction.begin();
            action.accept(session);
            session.flush();
            transaction.commit();
            session.close();
        } catch(RuntimeException e)
        {
            transaction.rollback();
            throw e;
        }
        
    }
    
    protected synchronized Session getSession()
    {
        return sessionFactory.openSession();
    }
    
    protected String getTableName()
    {
        return tableName;
    }
    
    public void close()
    {
        sessionFactory.close();
    }
}
