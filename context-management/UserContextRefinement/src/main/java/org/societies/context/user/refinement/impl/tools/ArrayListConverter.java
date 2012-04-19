package org.societies.context.user.refinement.impl.tools;

import java.util.ArrayList;

public class ArrayListConverter {

	public static <T1> ArrayList<T1> convertToSuperclass(ArrayList<? extends T1> t2s){
		ArrayList<T1> t1 = new ArrayList<T1>();
		for (T1 t2: t2s){
				t1.add((T1)t2);
		}
		return t1;
	}

	public static <T1,T2> ArrayList<T1> convertToSubclass(ArrayList<T2> t2s){
		ArrayList<T1> t1 = new ArrayList<T1>();
		for (T2 t2: t2s){
				t1.add((T1)t2);
		}
		return t1;
	}
}
