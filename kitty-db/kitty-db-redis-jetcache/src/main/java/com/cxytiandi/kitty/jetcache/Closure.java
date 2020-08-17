package com.cxytiandi.kitty.jetcache;

@FunctionalInterface
public interface Closure<O, I>  {
	O execute(I input);
}
