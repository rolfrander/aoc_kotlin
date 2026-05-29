# Advent of Code 2019

I started this quest of learning kotlin with 2019 because this year had a particularly fun set of tasks. A simple virtual machine
is implemented gradually through new tasks approx every other day. I am adding new functionality to the same implementation of
this machine as I work through the days, but I make sure to stay backward compatible so all previous days should continue working as I move along.

When I get closer to the end, there is a complete hack-style adventure game provided as input to the virtual machine, and by this stage
I am going to need to implement a full frontend to play the game. I have done this once before in clojure, this time it will be kotlin.

## Overview of tasks working on the INTCODE-vm

* [Day 2](https://adventofcode.com/2019/day/2): basic interpretation loop with two instructions: ADD and MUL. Read and write from and to memory, no IO. The caller defines input by changing memory before start, and reads output directly from memory after finish.
* [Day 5](https://adventofcode.com/2019/day/5): io with IN and OUT-operations. All input is defined before starting and all outputs are read after finish. Input implemented as a Sequence and output implemented as Array.
* [Day 7](https://adventofcode.com/2019/day/7): chaining VM-s together by output-input.
  * Part 1: still batch-oriented, one invocation at a time, output from one provides input to the next.
  * Part 2: concurrent operations. Different VM-instances needs to run simultaneously, continously reading input and writing output. Outputs from one needs to be fed into the input of the next.


## Details of concurrency design

Experiments in different branches:

* Branch: `intcode-sequece`
  * implement input and output as sequences which are filled lazily.
    This effectively converts the series of vm-invocations to a
    recursive call. This is elegant code, but for day 7 part 2 this
    didn't work, because the output of the last VM in the chain must be
    input to the first and we can't go back and change the input.
  * It might be possible to add a loop around this, manually moving
    output from the last VM to input of the first, but we still need
    some sort of signalling that the VM have stopped. I have a feeling
    this won't be that elegant...
* Branch: `intcode-coroutines`
  * Implementing input and output as channels between different VM-s
    running as separate coroutines.

