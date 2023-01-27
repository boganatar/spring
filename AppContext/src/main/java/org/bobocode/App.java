package org.bobocode;

import org.bobocode.context.ApplicationContext;
import org.bobocode.context.ApplicationContextImpl;

public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext context = new ApplicationContextImpl("org.bobocode");

        Bean1 bean1 = context.getBean(Bean1.class);
        System.out.println( "Got bean: " + bean1 );

        Bean2 bean2 = context.getBean(Bean2.class);
        System.out.println( "Got bean: " + bean2 );

        Bean3 bean3 = context.getBean("namedBean3", Bean3.class);
        System.out.println( "Got bean: " + bean3 );
    }
}
