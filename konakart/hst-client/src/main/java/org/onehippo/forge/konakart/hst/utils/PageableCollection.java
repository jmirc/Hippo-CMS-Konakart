package org.onehippo.forge.konakart.hst.utils;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoDocumentIterator;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

import java.util.ArrayList;
import java.util.List;

public class PageableCollection<T extends KKProductDocument> extends Pageable {

    public static final int DEFAULT_PAGE_FILL = 9;

    private List<T> items;
    private KKAppEng kkEngineIf;


    public PageableCollection(final HippoBeanIterator beans, final int pageSize, final int currentPage,
                              KKAppEng kkEngine) {
        super(beans.getSize(), currentPage, pageSize);
        this.kkEngineIf = kkEngine;

        items = new ArrayList<T>();
        process(beans);
    }

    public PageableCollection(final HippoDocumentIterator<T> beans, final int beanSize, final int pageSize,
                              final int currentPage, KKAppEng kkEngine) {
        super(beanSize, currentPage, pageSize);
        this.kkEngineIf = kkEngine;

        items = new ArrayList<T>();
        process(beans);
    }

    public PageableCollection(int total, List<T> items) {
        super(total);
        this.items = items;
    }

    private void process(HippoBeanIterator beans) {
        items = new ArrayList<T>();
        int startAt = getStartOffset();
        if (startAt < getTotal()) {
            beans.skip(startAt);
        }
        int count = 0;
        while (beans.hasNext()) {
            if (count == getPageSize()) {
                break;
            }
            Object bean = beans.next();
            if (bean != null) {
                T t = (T) bean;
                t.setKkEngine(kkEngineIf);
                items.add(t);
                count++;

            }
        }
    }

    private void process(HippoDocumentIterator<T> beans) {
        items = new ArrayList<T>();
        int startAt = getStartOffset();
        if (startAt < getTotal()) {
            beans.skip(startAt);
        }
        int count = 0;
        while (beans.hasNext()) {
            if (count == getPageSize()) {
                break;
            }
            Object bean = beans.next();
            if (bean != null) {
                T t = (T) bean;
                t.setKkEngine(kkEngineIf);
                items.add(t);
                count++;

            }
        }
    }

    public List<? extends HippoBean> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * Default page range for given page
     *
     * @param page current page
     * @return page surrounded by results on both side e.g.
     *         {@literal 1, 2, 3, 4<selected page>5, 6 ,7 ,8,9 etc.>}
     * @see #DEFAULT_PAGE_FILL
     */
    public List<Long> getPageRange(final int page) {
        return getPageRangeWithFill(page, DEFAULT_PAGE_FILL);
    }

    /**
     * Default Page range for current selected page, it is "google alike"
     * page range with x pages before selected item and x+1 after selected item.
     *
     * @return range based on default fill {@literal 1, 2, 3, 4, 5 <selected 6>, 7, 8,9 etc. }
     * @see #DEFAULT_PAGE_FILL
     */
    public List<Long> getCurrentRange() {
        return getPageRangeWithFill(getCurrentPage(), DEFAULT_PAGE_FILL);
    }

    /**
     * Return previous X and next X pages for given page, based on total pages.
     *
     * @param page   selected page
     * @param fillIn selected page
     * @return page range for given page
     */
    public List<Long> getPageRangeWithFill(long page, final int fillIn) {
        long currentPage = 0;
        final List<Long> pages = new ArrayList<Long>();
        // do bound checking
        if (page < 0) {
            currentPage = 1;
        }
        if (page > getTotalPages()) {
            currentPage = getTotalPages();
        }
        // fill in lower range: e.g. for 2 it will  be 1
        long start = currentPage - fillIn;
        if (start <= 0) {
            start = 1;
        }
        // end part:
        long end = currentPage + fillIn + 1;
        if (end > getTotalPages()) {
            end = getTotalPages();
        }
        for (long i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }
}