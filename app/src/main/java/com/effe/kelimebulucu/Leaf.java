package com.effe.kelimebulucu;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class Leaf
{
	// OnlyBanner
	// ca-app-pub-3294381374458559/3086365223
    private char m_Key;
    private boolean m_isWord;
    public List<Leaf> Children;
    public static Locale l = new Locale("tr");
    
    public Leaf(char key, boolean isWord)
    {
    	Children = new ArrayList<Leaf>();
        m_Key = String.valueOf(key).toUpperCase(l).charAt(0);
        m_isWord = isWord;
    }

    public char getKey()
    {
           return m_Key;
    }

    public boolean getIsWord()
    {
        return m_isWord;

    }

    public boolean isChildExists(char key)
    {
        for (int i = 0; i < Children.size(); i++)
            if (Children.get(i).getKey() == key)
                return true;
        return false;
    }

    public Leaf getChildbyKey(char key)
    {
        for (int i = 0; i < Children.size(); i++)
            if (Children.get(i).getKey() == key)
                return Children.get(i);
        return null;
    }
}
