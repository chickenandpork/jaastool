package org.smallfoot.jaas;

/*
 * @file
 *
 * related content for when I resume working on the non-eloquent part:
 * o http://www.massapi.com/source/gpd-3.3.1-r3035/gpd/plugins/org.jbpm.ui/src/org/jbpm/ui/sync/KerberosLoginConfiguration.java.html
 * o http://www.massapi.com/class/ap/AppConfigurationEntry.html
 * o https://wiki.debian.org/LDAP/Kerberos
 * o http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/security/krb5/Config.java#Config.getInstance%28%29
 * o http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7-b147/com/sun/security/auth/module/Krb5LoginModule.java/
 * o http://docs.oracle.com/javase/7/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/Krb5LoginModule.html
 * o http://docs.oracle.com/javase/6/docs/technotes/guides/security/jgss/lab/part1.html
 * o http://dmdaa.wordpress.com/2010/03/13/kerberos-setup-and-jaas-configuration-for-running-sun-jgss-tutorial-against-ad/
 * o http://docs.oracle.com/javase/6/docs/technotes/guides/security/jgss/lab/part5.html
 * o http://stackoverflow.com/questions/1214512/how-to-get-short-domain-name-from-dns-domain-name/1214751#1214751
 * o http://stackoverflow.com/questions/12606466/map-windows-domain-user-to-userkerberos-realm-in-java
 */

import com.sun.security.auth.callback.TextCallbackHandler;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.File;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;


public class JaasNoFile
{
    public class NoConfFileConfiguration extends Configuration
    {

        private AppConfigurationEntry appConfigurationEntry;

        NoConfFileConfiguration(String domain, String principal, String server, String password, boolean debug)
        {
//System.out.println(principal+"@"+domain+" @"+server+", password: "+password);
            //this map instance should have the same tokens as found in a krb.conf file
            Hashtable<String,Object> bogusConfigBlock = new Hashtable<String,Object> ();

            //java.util.Map<String,String> fileParams = new HashMap();
            // since this uses the Krb5LoginModule, a great resource can be found at: http://docs.oracle.com/javase/7/docs/jre/api/security/jaas/spec/com/sun/security/auth/module/Krb5LoginModule.html
            if (debug)
                bogusConfigBlock.put("debug","true");
            //bogusConfigBlock.put("tryFirstPass","true"); --> should have user/pass in config first
            //bogusConfigBlock.put("principal","allanc"); --> causes prompt for principal (defaulted to username) to be skipped

            // HADOOP-7489: -Djava.security.krb5.realm=OX.AC.UK -Djava.security.krb5.kdc=kdc0.ox.ac.uk:kdc1.ox.ac.uk
            // these values (sun.security.krb5.Config.java: sun.security.krb5.Config.Config(): @119) (http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/sun/security/krb5/Config.java#119)
            // define the "default_realm" and "kdc" per krb5.conf (ie http://aput.net/~jheiss/krbldap/files/krb5.conf)
            //
            // Either define both, or neither
            // note: not this:
            //bogusConfigBlock.put("java.security.krb5.realm","EXAMPLE.COM");
            //bogusConfigBlock.put("java.security.krb5.kdc","kdc0.example.com:kdc1.example.com");

            Hashtable<String,String> libdefaults = new Hashtable<String,String> ();
            if (null != principal)
                bogusConfigBlock.put("principal", principal);

            if (null == domain)
                libdefaults.put("default_realm", "EXAMPLE.COM");
            else
                libdefaults.put("default_realm", domain);

            if (null == server)
                libdefaults.put("kdc", "krb0.example.com:krb1.example.com".replace(':', ' '));
            else
                libdefaults.put("kdc", server.replace(':', ' '));

//for (java.util.Enumeration<String> e = libdefaults.keys(); e.hasMoreElements(); )
//{
//	String s = e.nextElement();
//	System.out.println("key: "+s+" -> V:"+libdefaults.get(s));
//}

            bogusConfigBlock.put("libdefaults", libdefaults);

            //define the configuration for later delivery
            appConfigurationEntry = new AppConfigurationEntry( "com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, bogusConfigBlock);
        }

        //offer out the configuration we have stored when the login process requests
        public AppConfigurationEntry[] getAppConfigurationEntry(String x)
        {
            if (!x.equalsIgnoreCase("client"))
                System.out.println("requested config block for "+x+"; I don't have that.  Giving the \"client\" block");

            return new AppConfigurationEntry[] { appConfigurationEntry };
        }

        /**
         * Interface method for reloading the configuration, which is not done in this config.  I'm trying to provide this to avoid "reloading" a config that is virtual and breaking the minds of the poor machines who want to reload from disk or such.  ..because disk is, like, so last week.
         */
        public void refresh() { }

    }



    boolean loginAndAction(String domain, String principal, String server, String password, boolean debug, boolean verbose, String processName, PrivilegedExceptionAction action)
    throws LoginException, PrivilegedActionException
    {

        /** This handler is used as the action to take when the login context is achieved -- when we have a TGT, execute this callback (which trivially spits out a text message) */
        CallbackHandler callbackHandler = new TextCallbackHandler();

        LoginContext context = null;

        try
        {
            // Create a LoginContext with a callback handler
            //context = new LoginContext(name, callbackHandler);
            // Create a LoginContext with a callback handler and a custom config
            context = new LoginContext(processName, null /* subject */, callbackHandler, new NoConfFileConfiguration(domain, principal, server, password, debug));

            // Perform authentication
            context.login();
        }
        catch (LoginException e)
        {
            System.err.println("Login failed");
            e.printStackTrace();
            return false;
        }

        // Perform action as authenticated user
        Subject subject = context.getSubject();
        if (verbose)
        {
            System.out.println(subject.toString());
        }
        else
        {
            System.out.println("Authenticated principal: " +
                               subject.getPrincipals());
        }

        Subject.doAs(subject, action);

        context.logout();
        return true;
    }

    // Action to perform
    static class TextConfirmAction implements PrivilegedExceptionAction
    {
        TextConfirmAction()
        {
        }

        public Object run() throws Exception
        {
            // Replace the following with an action to be performed
            // by authenticated user
            System.out.println("Secure access achieved; this authentication is confirmed.");
            return null;
        }
    }


    /** usage messages are useful to those of us with short memories as well (hey, I just need to add swap!) */
    public void usage(String proc)
    {
        System.out.println("Usage: "+proc+" -V|--version|-H|--help");

        System.out.println("     : "+proc+" [--debug|-D] [--verbose|-v] [--env|-E] [--fudgekdc|-f] [--fudgerealm|-F] ...");
        System.out.println("     : "+proc+" [--processname|-n] (defaults to \"client\")");
        System.out.println("     : "+proc+" [options] [--kdc|--server|-s <kdc/AD server>] [--realm|--domain|-d|-r <realm or domain>] [--principal|-u <username or principal@realm>]");
        System.out.println("   ie: "+proc+" -D -v -E");
        System.out.println("   ie: "+proc+" -DvE -u allan.clark -s dc01.example.com -d EXAMPLE.COM");
        System.out.println("   ie: "+proc+" -DvE --princpal=allan.clark --server=dc01.example.com --domain=EXAMPLE.COM");
        System.out.println("\n       "+proc+" will try all permutations of {principal}{domain}{server}");
        System.out.println("       "+proc+" -E gives two KDCs, one principal, and one realm, so 2 passwords will be prompted");
        System.out.println("       "+proc+" -DvE -u allan.clark -s dc01.example.com -d EXAMPLE.COM provides (2u x 3s x 2r) 12 combinations");
    }


    public static void main(String[] args) throws Exception
    {

        java.util.Vector<LongOpt> options = new java.util.Vector<LongOpt>(20,2);

        /* Always always ALWAYS provide a quick reference and a version output */
        options.add(new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'H'));
        options.add(new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V'));

        options.add(new LongOpt("promptpassword", LongOpt.OPTIONAL_ARGUMENT, null, 'P'));
        options.add(new LongOpt("kdc", LongOpt.REQUIRED_ARGUMENT, null, 's'));
        options.add(new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 's'));
        options.add(new LongOpt("domain", LongOpt.REQUIRED_ARGUMENT, null, 'd'));
        options.add(new LongOpt("env", LongOpt.NO_ARGUMENT, null, 'E'));
        options.add(new LongOpt("fudgekdc", LongOpt.NO_ARGUMENT, null, 'f'));
        options.add(new LongOpt("fudgerealm", LongOpt.NO_ARGUMENT, null, 'F'));
        options.add(new LongOpt("debug", LongOpt.NO_ARGUMENT, null, 'D'));
        options.add(new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'));
        options.add(new LongOpt("realm", LongOpt.REQUIRED_ARGUMENT, null, 'r'));
        options.add(new LongOpt("principal", LongOpt.REQUIRED_ARGUMENT, null, 'u'));
        options.add(new LongOpt("processname", LongOpt.REQUIRED_ARGUMENT, null, 'n'));

        //org.smallfoot.getopt.GetOpt g = new org.smallfoot.getopt.GetOpt("jaastool", args, options);
        Getopt g = new Getopt("jaastool", args, "HVP::s:d:FEDvr:u:n:", options.toArray(new LongOpt[1]));

        Vector<String> principals = new Vector<String>();
        Vector<String> passwords = new Vector<String>();
        Vector<String> servers = new Vector<String>();
        Vector<String> domains = new Vector<String>();
        boolean debug = false;
        boolean verbose = false;

        /** true == fudge the Realm into the environment.  Part of the not-yet-eloquent part, I was trying to avoid having to do that.  :( */
        boolean fudgeRealm = false;

        /** true == fudge the KDC into the environment.  Part of the not-yet-eloquent part, I was trying to avoid having to do that.  :(
	 */
        boolean fudgeKDC = false;

        String processName = "client";

        int c;
        while ((c = g.getopt()) != -1)
        {
            switch(c)
            {
            case 'P': /* prompt for and store a password */
                if (null != g.getOptarg())
                    passwords.add(g.getOptarg());
                break;

            case 'r': /* fallthru */
            case 'd': /* store a domain */
                domains.add(g.getOptarg());
                /* http://support.microsoft.com/kb/248807: Windows: realm name is always uppercase of kdc domain */
                /* http://stackoverflow.com/questions/1214512/how-to-get-short-domain-name-from-dns-domain-name/1214751#1214751 */
                break;
            case 's': /* store a kdc server */
                servers.add(g.getOptarg());
                break;
            case 'u': /* store a principal/username */
                principals.add(g.getOptarg());
                break;
            case 'n': /* change process name from default "client" */
                processName = g.getOptarg();
                break;

            case 'F': /* fudge default realm until it works */
                fudgeRealm = true;
                break;
            case 'f': /* fudge kdc until it works */
                fudgeKDC = true;
                break;

            case 'E': /* stuff values plucked form user's environment as though on a windows server */
            {
                /*
                 * Look for the following entries by running the 'set' command:
                 * LOGONSERVER=\\servername (This would be the Kerberos Server)
                 * USERDNSDOMAIN=PRODUCTION.SERVER.COM (This would be the DOMAIN)
                 * USERNAME=username (This would be the user ID, not the "Full Name")
                 */
                String s = (String) System.getenv("LOGONSERVER");
                String d = (String) System.getenv("USERDNSDOMAIN");
                String u = (String) System.getenv("USERNAME");
                s = s.replaceAll("[^-A-Za-z0-9\\.]","");
                d = d.toLowerCase();
                //u = u.toUpperCase();
                principals.add(u+"@"+d.toUpperCase());
                servers.add(s);
                servers.add(s+"."+d);

                fudgeKDC = true;
                fudgeRealm = true;
            }
            break;

            case 'D': /* set debug passed to underlying JAAS (shows actions taken by underlying JAAS and Kerberos plugin/auth) */
                debug = true;
                break;
            case 'v': /* set verbose passed to underlying JAAS (shows entire TGT content) */
                verbose = true;
                break;

                /*
                     * Follows is the "house-keeping": versions, usage, and the catch-all for bad options.
                     */
            case 'V':   // print the version and quit
            {
                System.out.println("Version 1.0 http://github.com/chickenandpork/jaastools");
                return;
            }

            default:
            case '?':
                // during build, this is just a dump of options; in shipping, this falls-thru to usage.
                //System.out.println("option \""+c+"\" selected");
                //System.out.println("long index = "+g.getLongind());

            case 'H':
                //new JaasNoFile().usage(g.progname());
                new JaasNoFile().usage("jaastool");
                return;
            }
        }

        // if no principal given, give it a null, and the underlying will prompt
        //if (1 > principals.size()) { System.out.println("FATAL: "+ /* g.progname() */ "jaastool" /* for now */ +" needs at least one \"principal\" (Kerberos-to-ActiveDirectory: username)"); System.exit (-1); }
        if (1 > principals.size()) principals.add(null);
        if (1 > servers.size())
        {
            System.out.println("FATAL: "+ /* g.progname() */ "jaastool" /* for now */ +" needs at least one \"server\" (Kerberos-to-ActiveDirectory: AD Server)");
            System.exit (-1);
        }

        // if no password given, give it a null, and the underlying will ask
        if (1 > passwords.size()) passwords.add(null);

        // if no domain given, give it a null, and the underlying will use default
        //if (1 > domains.size()) { System.out.println("FATAL: "+ /* g.progname() */ "jaastool" /* for now */ +" needs at least one \"realm\" (Kerberos-to-ActiveDirectory: domain)"); System.exit (-1); }
        if (1 > domains.size()) domains.add(null);

        PrivilegedExceptionAction action = new TextConfirmAction();

        Vector<String> accumulator = new Vector<String>();
        for (String domain: domains)
            for (String principal: principals)
                for (String server: servers)
                    for (String password: passwords)
                    {
                        if (fudgeKDC)
                            System.getProperties().setProperty("java.security.krb5.kdc", server);
                        if (fudgeRealm)
                        {
                            if (null != domain)
                                System.getProperties().setProperty("java.security.krb5.realm", domain);
                            else
                                System.getProperties().setProperty("java.security.krb5.realm", "EXAMPLE.COM");
                        }

                        accumulator.add("principal:"+principal+" (@"+domain+") via kdc "+server+" valid: "+new Boolean(new JaasNoFile().loginAndAction(domain, principal, server, password, debug, verbose, processName, action)).toString());
                    }

        System.out.println("KDC results:");
        System.out.println("============");
        for (String s: accumulator) System.out.println(s);
        System.out.println("============");
    }
}
