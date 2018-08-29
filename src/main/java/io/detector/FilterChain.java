package io.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Description: Make itself to be the last filter of the filter chain, and
 * return true without any determination.
 * </p>
 *
 * <p>
 * Company: 广州市俏狐信息科技有限公司
 * </p>
 *
 * @author Payne 646742615@qq.com
 *
 * @date 2016年1月16日 下午4:07:30
 *
 * @since 1.0.0
 */
public class FilterChain implements Filter {
	private final Collection<Filter> filters;
	private Iterator<Filter> iterator;

	/**
	 * construct a default resource filter chain
	 *
	 * @param filters
	 *            filter collection
	 */
	public FilterChain(Collection<Filter> filters) {
		this.filters = filters != null ? new ArrayList<Filter>(filters) : new ArrayList<Filter>();
		this.filters.add(this);
	}

	/**
	 * do next filtration, {@link NoSuchElementException} may thrown if your
	 * filter has illegal coding such as calling
	 * {@link FilterChain#doNext(Resource)} more than once
	 */
	public boolean doNext(Resource resource) {
		// without calling reset first? reset it!
		if (iterator == null) {
			reset();
		}
		return iterator.next().accept(resource, this);
	}

	/**
	 * reset the filter iterator for reuse
	 */
	public FilterChain reset() {
		iterator = filters.iterator();
		return this;
	}

	/**
	 * last filter, it is obviously for all filters accept this resource when
	 * this method was called, so return true.
	 */
	public boolean accept(Resource resource, FilterChain chain) {
		return true;
	}

}
