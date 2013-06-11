Examples
========

Hosts
-----
The hosts database contains simple host data attributed to host instances
divided into environment contexts.  The scotty template generates a un*x
style host table and using the contex passed in you can select environments.

    ./run.sh hosts
    ./run.sh hosts env=dev
    ./run.sh hosts 'env=qa|prod'

Simple
------
A simple example of straight variable replacement. The user.xml uses inheritance
and fuzy matching to get the `basic` account info.

    ./run.sh simple env=qa


