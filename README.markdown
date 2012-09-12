CWAC Prefs: For When Regular Preferences Just Aren't Enough
===========================================================

`SharedPreferences` are persisted in an XML file, in the internal storage
of your app. In many cases, this is perfectly fine. In other cases,
though, you might want to store those preferences in some other container.
This project provides another implementation of the `SharedPreferences`
interface, one where you can supply a strategy object to define how
the values should be loaded and persisted. Built-in strategies support
storing the values in SQLite, or encrypted in SQLCipher for Android.

This is available as a debug JAR file from the downloads area of this GitHub repo.
Note that if you wish to use the JAR *and* use the encrypted
preferences, you will also need a compatible edition of SQLCipher for Android.

The project itself is set up as an Android library project,
in case you wish to use the source code in that fashion. However, you will still
need to make a copy of the contents of the library's `assets/` directory in your
project, as assets are not automatically shared from a library project to its
host.

Usage
-----
Most of your usage of the `CWSharedPreferences` class (in `com.commonsware.cwac.prefs`)
will be the same as how you would use `SharedPreferences`. `SharedPreferences` is
an interface; `CWSharedPreferences` implements that interface. Hence, you still use
methods like `getInt()` to retrieve preference values, `edit()` to get an `Editor`
with setters like `putInt()`, and so on.

What differs is how you get the `CWSharedPreferences` object in the first place. You
will not get one from the standard `PreferenceManager` or via some method on `Context`.
Rather, you will use a `getInstance()` method on the `CWSharedPreferences` class, supplying
a strategy object to use if the `CWSharedPreferences` object does not yet exist. As with
`SharedPreferences`, `CWSharedPreferences` are held onto in a static data member and will
live for the life of your process, treated as a singleton on a per-name basis.

### Defining and Using a Strategy

The static `getInstance()` method on `CWSharedPreferences`, to give you a `CWSharedPreferences`
object, takes a `CWSharedPreferences.StorageStrategy` object as a parameter. This interface
defines where and how the preference values are loaded and stored.

There are two standard implementations of `StorageStrategy`:

- `SQLiteStrategy`, which stores its data in a SQLite database
- `SQLCipherStrategy`, which stores its data in a SQLCipher for Android encrypted database

The constructor for `SQLiteStrategy` takes three parameters:

- a `Context` object for use with an internal `SQLiteOpenHelper` implementation
- a string representing the name of the database file to use (also serves as a unique key for
the static cache of `CWSharedPreferences` instances)
- a `LoadPolicy`, described in greater detail below

The constructor for `SQLCipherStrategy` takes those parameters, plus the password for the
database. This password will be used to encrypt the database if this is no existing database,
plus it is used to decrypt the database as needed over time.

### Load Policies

The `LoadPolicy` indicates what should happen when the `CWSharedPreferences` is created
and needs to load its data. There are three possible `LoadPolicy` enum values:

- `SYNC` means that the `CWSharedPreferences` will load its in-memory cache of the preference
values on the current thread. Use this if you are using your own background thread to initialize the
`CWSharedPreferences` instance and do not need `CWSharedPreferences` to fork
its own thread for this purpose.

- `ASYNC_BLOCK` is the same basic behavior you see with the stock implementation
of `SharedPreferences`: the data is loaded in a background thread, but your
attempt to read that data blocks until the load is complete.

- `ASYNC_EXCEPTION` will throw a `RuntimeException` if you attempt to read
from a `CWSharedPreferences` where the load has not yet completed.

`SYNC` is generally the best answer, as you are assured that you will not block
the main application thread. `ASYNC_BLOCK` *could* block the main application
thread if you try using the preferences right away and they have not been loaded
yet.

So, for example, you could create a `SQLiteStrategy` object via:

    new SQLiteStrategy(getContext(), NAME, LoadPolicy.SYNC);

(assuming a static data member named `NAME` that is the database name, and
assuming the existence of some `getContext()` method to return a `Context`)

Then, you could retrieve a `CWSharedPreferences` via:

    CWSharedPreferences.getInstance(new SQLiteStrategy(getContext(), NAME, LoadPolicy.SYNC));

### Limitations

Due to some [unfortunate limitations](http://code.google.com/p/android/issues/detail?id=36967)
in the way `PreferenceFragment` and `PreferenceActivity` work, there is no known
simple way to use `CWSharedPreferences` with these classes.

Dependencies
------------
To use the project with `SQLCipherStrategy`, or to build the project
from source (including using it as an Android library project), you
will need to download and install SQLCipher for Android. The library
project already has a compatible edition, so if you clone the repo and
use the library project *in situ*, you should be OK. If you wish to
use the JAR instead, visit [the SQLCipher for Android](http://sqlcipher.net/sqlcipher-for-android/)
Web site to learn more about how to add it to your project.

Other than that, there are no dependencies for the main project.
Notably, if you are using the JAR and not using `SQLCipherStrategy`,
you should not need SQLCipher for Android.

Version
-------
This is version v0.0.2 of this module, meaning that this is a proof
of concept, seeking input from interested parties.

Demo
----
Normally, CWAC projects have a `demo/` subproject. This one does not.
In part, that is due to the UI limitations mentioned above.

It does, however, have a `tests/` sub-project that implements a JUnit
test suite for exercising basic aspects of the API. Note, though, that
these tests are "white box", insofar as the tests live in the same
package as `CWSharedPreferences` and access package-private methods.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Getting Help
------------
Do not ask for help via Twitter.

And, for those of you who skipped over that sentence: do not ask for help on Twitter. Anyone who thinks that
developer support can be handled in 140-character chunks should not be attempting to use a CWAC component.

Now, that being said, the rest of your help will be for bugs or questions.

### Bugs

If you are experiencing some sort of problem using this component, where you are fairly certain the component
itself is at fault, please submit a pull request
for a new test case to be added to the `tests/` project that demonstrates the problem. Or, create
a sample project that demonstrates the problem, post the source code to
that project somewhere (e.g., a public GitHub repo), and file an
[issue](https://github.com/commonsguy/cwac-endless/issues), pointing to your project and providing
instructions on how to reproduce the problem.

Do not file an issue if you cannot reproduce the problem, or with only partial source code that may or may
not be related to the problem.

### Feature Requests and Non-Bug Issues

If you see likely flaws just by looking at the code, or you see places where we
would really need additional stuff for this to be useful, please file an
[issue](https://github.com/commonsguy/cwac-endless/issues).

### Other Questions

If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware` and `android`. Be sure to indicate
what CWAC module you are having issues with, and be sure to include *relevant* source code 
and stack traces if you are encountering crashes. 

Release Notes
-------------
* v0.0.2: minor issue fixes
* v0.0.1: Initial import

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

