# OLORM-52: On the balance between design and implementation when we build software

> Dette innlegget er krysspublisert til Mikrobloggeriet, originalen ligger på Teodors personlige nettside: https://play.teod.eu/software-design-implementation-balance/

X: "design"? You mean the work designers do?

T: No. I mean software design. I have a beef with the current usage of
the word "design". We're using it vaguely! I used to do design of steel
structures and design of concrete structures. These days I often engage
in software design.

I feel like the agile movement inadvertently dragged in an assumtion
that design is bad, and writing code is better. I think "don't do
design, write code instead" is a *really bad idea*.

X: So ... Why now?

T: I'm working with two great people. Having a lot of fun. But I feel
like we struggle a lot finding a balance between design and
implementation. Right now, we talk a lot about implementation details,
but *not* about system design.

X: Ah, I get it! You're talking about architecture! Software
architecture!

T: 😅 I ... have a bit of a beef with that word too.

X: why?

T: We software developers took the word "architecture" from civil
engineering projects, and I fear that we took the wrong word. In
software, we want to talk about the *core* of our system. Architects
*can do* the start of the work, if what you care about is *floor plan
utilization*, and the civil engineering is trivial. Architects *do not*
build bridges. Architects *do not* build dams. Architects *do not* build
industrial plants. In those cases, the civil engineers (or construction
engineers) hold the "core".

X: so ... which word, then, if architecture is bad?

T: I think the word is "design". And for systems, "system design"!

X: okay. Man, when you *insist* on redefining words before you even
start speaking, it sometimes rubs be the wrong way. It's like everything
I say is wrong, right?

T: Yeah, I know. Sorry about that. I don't know of a better way ---
other than leaning completely into the arts, presenting ideas as
theater, dialogue or as novels. Steal from [Eliyahu
Goldratt](https://play.teod.eu/eliyahu-goldratt/)'s way of presenting things, perhaps.

X: yeah, yeah. I don't always have time for that, you know?

T: Yeah, there's *so much f-in stuff*. I feel like we could make due
with *less stuff*. But that requires some thinking.

X: So ... What was that balance you mentioned? A balance between design
and implementation?

T: Right. Thanks. That was where we started.

We spend *so much time* on implementation and *so little time on
design*. And we're calling it "agile". "agile" as an excuse for coding
up things when we don't have any idea why we have to do the things we
do. If we slow down, we might get pressure to speed up. It's lean to do
less stuff. But rather than cut the problems we don't care about, we
solve the problems we care about badly!

This is where design comes in. We should *know what our goal is*. I'm
... I'm at a point where I have no interest in writing code unless the
goal is clear.

X: what about teamwork? Everyone needs to know what do do, right?

T: Yeah, that's the hard part. It's harder to solve real problems than
write code. And it's *even harder* when you're a team. So much shared
context is needed.

X: So, what do we do?

T: I'm discovering this myself as I go along. I've had success with two
activities.

One activity is pair programming. This one is hard. Knowing when to
focus on design and when to focus on implementation is hard. I think
great pairing is something you have to re-learn every time you pair with
someone new. Without trust, this will simply fail. And that trust needs
to go both ways. I need to trust you, and you need to trust me.

Another activity is to use a decision matrix to compare approaches to
solve a problem. A decision matrix lets you do clean software design
work without getting stuck in all the details.

X: How should I learn pair programming?

T: Ideally, you get to pair with someone who is good at pair
programming. I had the chance to pair with [Lars
Barlindhaug](https://play.teod.eu/lars-barlindhaug/) in 2019 and [Oddmund
Strømme](https://play.teod.eu/oddmund-stromme/) in 2020. From Lars, I learned that it's
better to organize your code into modules where each module solves a
problem you care about. From Oddmund, I learned that I could work in
smaller increments.

If you *do not* have someone you can learn pairing from on your team,
watch [Magnar Sveen](https://play.teod.eu/magnar-sveen/) and [Christian
Johansen](https://play.teod.eu/christian-johansen/) pair from their youtube screencasts:
<https://www.parens-of-the-dead.com/>

X: And ... those decision matrices?

T: Watch [Rich Hickey](https://play.teod.eu/rich-hickey/) explain decision matrices in
[Design in Practice](https://play.teod.eu/design-in-practice/). Then try it out with your
team!

------------------------------------------------------------------------

Thank you to [Christian Johansen](https://play.teod.eu/christian-johansen/) for giving
feedback on an early version of this text.

---Teodor
