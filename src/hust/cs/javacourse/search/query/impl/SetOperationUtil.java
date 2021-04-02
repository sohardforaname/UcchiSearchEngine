package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.query.AbstractHit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetOperationUtil {
    static List<AbstractHit> getIntersection(AbstractHit[] leftHitList, AbstractHit[] rightHitList) {
        int i = 0, j = 0;
        List<AbstractHit> hitList = new ArrayList<>();
        while (i < leftHitList.length && j < rightHitList.length) {
            if (leftHitList[i].getDocId() == rightHitList[j].getDocId()) {
                AbstractHit currentHit = new Hit(leftHitList[i]);
                currentHit.getTermPostingMapping().putAll(rightHitList[j].getTermPostingMapping());
                hitList.add(currentHit);
                ++i;
                ++j;
            } else if (leftHitList[i].getDocId() < rightHitList[j].getDocId()) {
                ++i;
            } else {
                ++j;
            }
        }
        return hitList;
    }

    static List<AbstractHit> getUnion(AbstractHit[] leftHitList, AbstractHit[] rightHitList) {
        int i = 0, j = 0;
        List<AbstractHit> hitList = new ArrayList<>();
        while (i < leftHitList.length && j < rightHitList.length) {
            if (leftHitList[i].getDocId() == rightHitList[j].getDocId()) {
                AbstractHit currentHit = new Hit(leftHitList[i]);
                currentHit.getTermPostingMapping().putAll(rightHitList[j].getTermPostingMapping());
                hitList.add(currentHit);
                ++i;
                ++j;
            } else if (leftHitList[i].getDocId() < rightHitList[j].getDocId()) {
                AbstractHit currentHit = new Hit(leftHitList[i]);
                hitList.add(currentHit);
                ++i;
            } else {
                AbstractHit currentHit = new Hit(rightHitList[j]);
                hitList.add(currentHit);
                ++j;
            }
        }
        hitList.addAll(Arrays.asList(leftHitList).subList(i, leftHitList.length));
        hitList.addAll(Arrays.asList(rightHitList).subList(j, rightHitList.length));
        return hitList;
    }

    static AbstractHit[] getIntersectionReturnArray(AbstractHit[] leftHitList, AbstractHit[] rightHitList) {
        List<AbstractHit> list = getIntersection(leftHitList, rightHitList);
        AbstractHit[] hitArray = new Hit[list.size()];
        return list.toArray(hitArray);
    }

    static AbstractHit[] getUnionReturnArray(AbstractHit[] leftHitList, AbstractHit[] rightHitList) {
        List<AbstractHit> list = getUnion(leftHitList, rightHitList);
        AbstractHit[] hitArray = new Hit[list.size()];
        return list.toArray(hitArray);
    }
}
