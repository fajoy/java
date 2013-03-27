import java.util.*;

public class CombinationHelper {
	private int item_count;
	private int min_count;
	private int max_count;
	private List<int[]> ln = new ArrayList<int[]>();
	private List<int[]> newln = new ArrayList<int[]>();
	private int i = 0;
	private int item_count_1;
	private int combLen_1 = 0;
	private int combLen0 = 1;
	private int combLen1 = 2;
	private int[] comb;

	public CombinationHelper(int item_count, int min_count, int max_count) {
		if (max_count > item_count)
			max_count = item_count;
		if (min_count < 1)
			min_count = 1;
		this.min_count = min_count;
		this.max_count = max_count;
		this.item_count = item_count;
		this.nowAcion = firstAction;
		item_count_1 = item_count - 1;

		if (min_count > max_count || max_count < 1) {
			nowAcion = finalAction;
			return;
		}
		if (min_count > 1) {
			while (combLen1 != min_count) {
				nowAcion.next();
			}
		}
	}

	private NextAction nowAcion = null;

	private NextAction firstAction = new NextAction() {
		@Override
		public int[] next() {
			int[] items = null;
			if (i < item_count_1) {
				items = new int[1];
				items[0] = i;
				ln.add(items);
				i++;
			} else {
				nowAcion = secondAction;
				if (combLen1 > max_count)
					nowAcion = finalAction;
				items = new int[1];
				items[0] = i;
			}
			return items;
		}
	};

	private NextAction secondAction = new NextAction() {
		@Override
		public int[] next() {
			if (ln.size() > 0) {
				newln = new ArrayList<int[]>();
				comb = ln.get(0);
				ln.remove(comb);
				i = comb[combLen_1] + 1;
				nowAcion = thridAction;
			} else {
				nowAcion = finalAction;
			}
			return nowAcion.next();
		}
	};

	private NextAction thridAction = new NextAction() {
		@Override
		public int[] next() {
			int[] items = null;
			if (i < item_count_1) {
				items = Arrays.copyOf(comb, combLen1);
				items[combLen0] = i;
				newln.add(items);
				i++;
			} else {
				items = Arrays.copyOf(comb, combLen1);
				items[combLen0] = item_count_1;
				if (ln.size() > 0) {
					comb = ln.get(0);
					ln.remove(comb);
					i = comb[combLen_1] + 1;
				} else {
					ln = newln;
					combLen_1++;
					combLen0++;
					combLen1++;
					nowAcion = secondAction;
					if (combLen1 > max_count)
						nowAcion = finalAction;
				}
			}
			return items;
		}
	};
	private NextAction finalAction = new NextAction() {
		@Override
		public int[] next() {
			ln = null;
			newln = null;
			return null;
		}
	};

	public int[] next() {
		return nowAcion.next();
	}

	private interface NextAction {
		public int[] next();
	}

	public static List<int[]> getCombinationIndex(int size) {
		List<int[]> result = new ArrayList<int[]>();
		List<int[]> ln = new ArrayList<int[]>();
		int n_1 = size - 1;
		for (int i = 0; i < n_1; i++) {
			int[] items = new int[1];
			items[0] = i;
			result.add(items);
			ln.add(items);
		}
		int[] i_1 = new int[1];
		i_1[0] = n_1;
		result.add(i_1);

		int len_1 = -1;
		int len0 = 0;
		int len1 = 1;
		while (ln.size() > 0) {
			List<int[]> newln = new ArrayList<int[]>();
			len_1++;
			len0++;
			len1++;
			for (int[] comb : ln) {
				for (int p = comb[len_1] + 1; p < n_1; p++) {
					int[] items = Arrays.copyOf(comb, len1);
					items[len0] = p;
					result.add(items);
					newln.add(items);
				}
				int[] last_item = Arrays.copyOf(comb, len1);
				last_item[len0] = n_1;
				result.add(last_item);
			}
			ln = newln;
		}
		return result;
	}
}
/*
 * L1 a:0 b:1 c:2 d:3
 * 
 * L2 a{b,c,d} 0 1~3 ab 1 ac 2 ad 3 b{c,d} 1 2~3 bc 2 bd 3 c{d} 2 3~3 cd 3
 * 
 * L3 ab{c,d} 1 2~3 abc 2 abd 3 ac{d} 2 3~3 acd 3 bc{d} 2 3~3 bcd 3
 * 
 * L4 abc{d} 2 3~3 abcd 3
 */
