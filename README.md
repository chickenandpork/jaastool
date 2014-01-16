jaastool
========

JAAS Tools used to aid diagnosing opaque issues


BUILDING

This is a standard AutoTools build, so:

1) autoreconf -vfi

2) make install (or "make check" to include that plus run the tests)

3) make rpm (if so inclined)


java/jaastool.jar is the proper built jar file; convjars is where "convenience jars" are built if 
you're less concerned with license purity and more concerned with:

1) your tests are bogus.  Let me test exactly what you tested; or

2) I need it working like yesterday.  Holy crap please help me.
      Gimme something to download immediately to make the pain stop.


We've all been there.  In both cases.  grab convjars/jaastool.jar, it's not sanitary, but it works.


USAGE

The only portion right is the JaasNoFile.java which nearly works as eloquently as implied.  I use
it for trying to see why a Kerberos client who shall remain nameless (VW-3) is not getting a clear
result from the Kerberos server and/or not logging the full story.  Also, said client is a great
product, but in cases of customer doubt or dialect/terminology skew, this utility is a third-party
to that argument.

    java -jar jaastool.jar -E

For more detail: 

    java -jar jaastool.jar -EDv

JaasTool will then grab your username and domain as Principal and Realm, hit your LOGONSERVER, and
report the details verbosely.  There will be a summary after it's all done.

 o You'll be asked multiple times for your password.
     Don't worry, to avoid storing it, JaasTool re-asks.

 o There will be a lot of java exception and Kerberos stuff.
     Don't worry, just wait for the summary at the end

 o SERIOUSLY, do the password thing repeatedly until the summary.  Details are all through that text.

The Sumary will tell you which principal@realm works at which servers (OK, which username@domain)

The code is hereby pasted to allow inspection and improve trust.  Kerberos is used in secure
environments.  I get that, so I'm "opening the trenchcoat to show nothing stashed inside".


Now go, get what you need, fix your pain!
