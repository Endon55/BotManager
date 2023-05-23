package core.database.access;

import core.database.tables.Note;
import org.hibernate.SessionFactory;

public class Notes extends Dao<Note, Integer>
{
    public Notes(SessionFactory sessionFactory)
    {
        super(sessionFactory, Note.class, "Notes");
    }
    
    
    
}
