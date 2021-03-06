package com.oath.cyclops.control;

import static cyclops.companion.Optionals.visit;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Optional;

import cyclops.reactive.ReactiveSeq;
import org.junit.Test;

import cyclops.reactive.collections.mutable.ListX;
import cyclops.companion.Optionals;

public class OptionalsTest {
	@Test
	public void testSequence() {
		Optional<ReactiveSeq<Integer>> maybes =Optionals.sequence(ListX.of(Optional.of(10),Optional.of(20),Optional.of(1)));
		assertThat(maybes.map(s->s.toList()),equalTo(Optional.of(ListX.of(10,20,1))));
	}
	@Test
    public void testSequencePresent() {
        Optional<List<Integer>> maybes =Optionals.sequencePresent(ListX.of(Optional.of(10),Optional.empty(),Optional.of(1))).map(s->s.toList());
        assertThat(maybes,equalTo(Optional.of(ListX.of(10,1))));
    }
	@Test
    public void testSequenceEmpty() {
        Optional<ReactiveSeq<Integer>> maybes =Optionals.sequence(ListX.of(Optional.of(10),Optional.empty(),Optional.of(1)));
        assertThat(maybes,equalTo(Optional.empty()));
    }

    @Test
    public void visitTest() {


        Optional<Integer> empty = Optional.empty();
        Optional<Integer> present = Optional.of(10);

        int a = visit(empty, i -> i + 10, () -> -1);
        int b = visit(present, i -> i + 10, () -> -1);


        assertThat(a,equalTo(-1));
        assertThat(b,equalTo(20));
    }

    public void problem(){


        Optional<Integer> empty = Optional.empty();
        Optional<Integer> present = Optional.of(10);


        //direct access - is not present
        empty.get();


        Integer a = null;
        if(present.isPresent()){
            a = present.get();
        }
        //empty case is not handled - a can be null




    }

}
