package cyclops.data.basetests;

import cyclops.collections.AbstractIterableXTest;
import cyclops.reactive.collections.mutable.ListX;
import cyclops.companion.Monoids;
import cyclops.control.Option;
import cyclops.data.ImmutableList;
import cyclops.data.Seq;
import cyclops.reactive.ReactiveSeq;
import cyclops.reactive.Spouts;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public abstract class BaseImmutableListTest extends AbstractIterableXTest {

    protected abstract <T> ImmutableList<T> fromStream(Stream<T> s);

    @Override
    public abstract <T> ImmutableList<T> of(T... values);
    public abstract <T> ImmutableList<T> empty();
    @Test
    public void emptyUnit(){
        assertTrue(of(1,2,3).emptyUnit().isEmpty());
    }
    @Test
    public void getOrElseMulti(){
        assertThat(of(1).getOrElse(0,null),equalTo(1));
        assertThat(of(1,2).getOrElse(1,null),equalTo(2));
        assertThat(of(1,2,3).getOrElse(1,null),equalTo(2));
    }
    @Test
    public void get(){
        assertThat(of(1,2,3,4).get(2),equalTo(Option.some(3)));
        assertThat(of(1,2,3,4).get(20),equalTo(Option.none()));
        assertThat(of(1,2,3,4).get(-5),equalTo(Option.none()));
    }
    @Test
    public void getOrElse(){
        assertThat(of(1,2,3,4).getOrElse(2,-1),equalTo(3));
        assertThat(of(1,2,3,4).getOrElse(20,-1),equalTo(-1));
        assertThat(of(1,2,3,4).getOrElse(-5,100),equalTo(100));
    }
    @Test
    public void getOrElseGet(){
        assertThat(of(1,2,3,4).getOrElseGet(2,()->-1),equalTo(3));
        assertThat(of(1,2,3,4).getOrElseGet(20,()->-1),equalTo(-1));
        assertThat(of(1,2,3,4).getOrElseGet(-5,()->100),equalTo(100));
    }
    @Test
    public void testMapA(){
        assertThat(of(1,2,3).map(i->i*2),equalTo(Seq.of(2,4,6)));
        assertThat(this.<Integer>empty().map(i->i*2),equalTo(Seq.empty()));
    }
    @Test
    public void testFlatMapA(){
        assertThat(of(1,2,3).flatMap(i-> of(i*2)),equalTo(Seq.of(2,4,6)));
        assertThat(this.<Integer>empty().concatMap(i-> of(i*2)),equalTo(Seq.empty()));
    }

    @Test
    public void testFoldRightA(){

        assertThat(fromStream(ReactiveSeq.range(0,100_000)).foldRight(Monoids.intSum),equalTo(704982704));
    }
    @Test
    public void forEach2() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), (a , b) -> a + b).toList().size(),
                equalTo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 3, 4, 5, 6, 7, 8,
                        9, 10, 11, 12).size()));
    }

    @Test
    public void forEach2Filter() {

        assertThat(of(1, 2, 3).forEach2(a -> Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), (a , b) -> a > 2 && b < 8,
                (a ,b) -> a + b).toList().size(), equalTo(Arrays.asList(3, 4, 5, 6, 7, 8, 9, 10).size()));
    }

    @Test
    public void toStringTest(){
        assertThat(of().toString(),equalTo("[]"));
        assertThat(of(1).toString(),equalTo("[1]"));
    }
    @Test
    public void replaceFirstTest(){
        assertThat(of(1,2,3).replaceFirst(2,3),equalTo(of(1,3,3)));
        assertThat(of(1,2,2,3).replaceFirst(2,3),equalTo(of(1,3,2,3)));
        assertThat(empty().replaceFirst(2,3),equalTo(of()));
    }
    @Test
    public void onEmptyThrow(){

        assertTrue(empty().onEmptyTry(()->new RuntimeException("hello")).isFailure());
        assertFalse(of(1,2).onEmptyTry(()->new RuntimeException("hello")).isFailure());
    }

  @Test
  public void viewTest(){
      List<Integer> list = of(1,2,3).listView();
      assertThat(list.size(),equalTo(3));
      assertThat(list,equalTo(Arrays.asList(1,2,3)));

      assertThat(list.add(1),equalTo(false));
      assertThat(list.addAll(Arrays.asList(1)),equalTo(false));
      assertThat(list.addAll(1,Arrays.asList(1)),equalTo(false));
      assertThat(list.contains(2),equalTo(true));
      assertThat(list.containsAll(Arrays.asList(2,3)),equalTo(true));
      assertThat(list.containsAll(Arrays.asList(2,3,4)),equalTo(false));
      assertThat(list.remove(1),equalTo(2));
      assertThat(list.remove((Object)1),equalTo(false));
      assertThat(list.removeAll(Arrays.asList(1)),equalTo(false));
    }
  @Test
  public void mergeMap(){
    for(int l=0;l<100_000;l++) {
      System.out.println("************Iteration " + l);
      System.out.println("************Iteration " + l);
      System.out.println("************Iteration " + l);

      Assert.assertThat(this.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .mergeMap(i -> of(i, i * 2, i * 4)
          .mergeMap(x -> of(5, 6, 7))).toListX().size(),
        equalTo(ListX.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
          5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6
          , 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7
          , 7, 7, 7, 7, 7).size()));

    }
  }
  @Test
  public void mergeMap3(){
    for(int l=0;l<100_000;l++) {
      System.out.println("************Iteration " + l);
      System.out.println("************Iteration " + l);
      System.out.println("************Iteration " + l);

      Assert.assertThat(this.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        .mergeMap(3,i -> Spouts.of(i, i * 2, i * 4)
          .mergeMap(3,x -> of(5, 6, 7))).toListX().size(),
        equalTo(ListX.of(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7).size()));

    }
  }
}
