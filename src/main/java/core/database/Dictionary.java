package core.database;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

@Log4j2
public class Dictionary
{
    public static final int WORD_COUNT = 127142;
    private final URL DICTIONARY_PATH = this.getClass().getResource("/dictionary/Dictionary.txt");
    
    /**
     *
     * @param index 0 indexed
     * @return
     */
    public String getWord(int index)
    {
        if(index < 0) throw new RuntimeException("Dictionary index cannot be less than 0");
        if(index >= WORD_COUNT) throw new RuntimeException("Dictionary index cannot be greater than " + WORD_COUNT);
    
        try
        {
            assert DICTIONARY_PATH != null;
            return FileUtils.readLines(new File(DICTIONARY_PATH.getFile()), Charset.defaultCharset()).get(index);
        } catch(IOException e)
        {
            log.error(e);
        }
        return "";
    }
    
}
