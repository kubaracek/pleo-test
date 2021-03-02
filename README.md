## Preparation Phase

To keep things simple I initially draw this to give myself an idea what I want to build.

![Architecture](images/initial-architecture.jpg)

I took this as an oportunity to test how I would use Kotlin working on my personal projects.
I really have no idea if this is *the Kotlin way* or if the community prefers to write the code a bit more imperative.
What I found is that Kotlin shines best (for me) by the ability of combining both functional and object oriented patterns.

Keep in mind this is my first Kotlin code :))

## Things that kept me busy :)

 1) Exposed! I was strugling to find some easy to follow documentation and this kept me busy probably the longest :))
   - I'm pretty sure that the way I'm preloading Charges to an Invoice is not the most efficient and there's probably a single method that does that for me
 2) DateTime. I was spending too much time trying to combine Exposed and Java's datetime so I decided to go with Joda even though I wanted to keep this dependency less :))
 3) Kotlin has Char! I got stucked for good 20 minutes fighting a compiler error when I realised that it all comes down to a second error where the compiler been complainin about using single quote around whole string :D

## Multinode

Straight from the beginning I knew this is the 'Achilles heel' when running this in production.
Things that popped up to my mind Kafka :hearth: or Akka sound .. it sounded .. logical. 

I specifically didn't spend too much time on this as in production environment that would be a bit different setup.
And installing Kafka or Akka would be an extremely hard dependency for the test task :))

My initial thinking would be to use Kafka, then I realised that Kotlin is a JVM so Akka could be an option too
A Single producer with many consumers where message UID being the id of the Invoice - to avoid duplicate processing.


## What I think of Kotlin

**The Good**
  - Algebraic type system!
  - Has many functional principles
  - 'easier' Scala but still very powerful
  - Fairly easy to pick-up. Knowing a bit of Scala and Haskell helps a lot
  - Compiler gives helpful error messages ( Unlike Haskell for example :D )

**The bad**
  - Would like to see more functional types provided by the language. Eg: Either or Maybe I was really missing (they are easy to implement though)
  - I do struggle finding documentation for libraries.
  - jvm :))
  - Would be great to have a function prefix that would mark function without a side effect (pure)
  - I do not fully understand the type system hirearchy, need to read a bit more about that. Eg companion objects, how to properly extend SUM type etc.

I'm actually so happy with the language that I'm considering using it as my primary language for quick personal projects :party:
