package com.framework.pages;

import io.qameta.allure.Step;

public class CommentHookPage extends BasePage {

    private static final String BASE_URL = "https://commenthook.com";

    // Navigation
    private static final String NAV           = "nav";
    private static final String LOGO_LINK     = "header a[href='/']";
    private static final String NAV_LINK      = "nav a:has-text('%s')";

    // Hero
    private static final String HERO_HEADING  = "h1";
    private static final String HERO_DESC     = "main p:has-text('qualifies leads')";

    // CTA links
    private static final String CTA_BY_TEXT   = "a:has-text('%s')";

    // How-it-works / features section headings
    private static final String SECTION_H2    = "h2:has-text('%s')";
    private static final String STEP_H3       = "h3:has-text('%s')";

    // Pricing
    private static final String PLAN_HEADING  = "h3:has-text('%s')";
    private static final String PLAN_PRICE    = "h3:has-text('%s') + p";
    private static final String POPULAR_BADGE = "p:has-text('Most popular')";
    private static final String PRICING_GET_STARTED = "section a:has-text('Get started'), main a:has-text('Get started')";

    // Blog
    private static final String ARTICLE_LINK  = "main a[href*='/blog/']";

    // Footer
    private static final String FOOTER_TEXT   = "footer";
    private static final String FOOTER_LINK   = "footer a:has-text('%s')";

    @Step("Open CommentHook homepage")
    public CommentHookPage open() {
        navigateTo(BASE_URL + "/");
        return this;
    }

    @Step("Open CommentHook path: {path}")
    public CommentHookPage openPath(String path) {
        navigateTo(BASE_URL + path);
        return this;
    }

    public String title() {
        return getTitle();
    }

    @Step("Get hero heading text")
    public String heroHeading() {
        return getText(HERO_HEADING);
    }

    @Step("Check hero description mentions '{keyword}'")
    public boolean heroDescriptionContains(String keyword) {
        return isVisible(HERO_DESC);
    }

    @Step("Get href of nav link '{label}'")
    public String navLinkHref(String label) {
        return getAttribute(NAV_LINK.formatted(label), "href");
    }

    @Step("Click nav link '{label}'")
    public void clickNavLink(String label) {
        click(NAV_LINK.formatted(label));
        page().waitForLoadState();
    }

    @Step("Get href of CTA '{label}'")
    public String ctaHref(String label) {
        return getAttribute(CTA_BY_TEXT.formatted(label), "href");
    }

    @Step("Check section heading '{heading}' is visible")
    public boolean isSectionHeadingVisible(String heading) {
        return isVisible(SECTION_H2.formatted(heading));
    }

    @Step("Check step '{step}' is visible")
    public boolean isStepVisible(String step) {
        return isVisible(STEP_H3.formatted(step));
    }

    @Step("Check feature '{feature}' is visible")
    public boolean isFeatureVisible(String feature) {
        return isVisible(STEP_H3.formatted(feature));
    }

    @Step("Check pricing plan '{plan}' is visible")
    public boolean isPlanVisible(String plan) {
        return isVisible(PLAN_HEADING.formatted(plan));
    }

    @Step("Get price text for plan '{plan}'")
    public String planPrice(String plan) {
        return getText(PLAN_PRICE.formatted(plan));
    }

    @Step("Check plan '{plan}' has 'Most popular' label")
    public boolean isPlanMostPopular(String plan) {
        String selector = "h3:has-text('%s')".formatted(plan);
        // The badge is a sibling paragraph before the heading within the same container
        return page()
                .locator(selector)
                .locator("xpath=ancestor::*[position()=1]")
                .locator(POPULAR_BADGE)
                .isVisible();
    }

    @Step("Count 'Get started' buttons in pricing section")
    public int pricingGetStartedCount() {
        return getCount(PRICING_GET_STARTED);
    }

    @Step("Check all 'Get started' buttons href equal '{expected}'")
    public boolean allGetStartedLinksPointTo(String expected) {
        int count = pricingGetStartedCount();
        for (int i = 0; i < count; i++) {
            String href = page().locator(PRICING_GET_STARTED).nth(i).getAttribute("href");
            if (!expected.equals(href)) {
                return false;
            }
        }
        return count > 0;
    }

    @Step("Check blog has at least one article link")
    public boolean hasBlogArticles() {
        return getCount(ARTICLE_LINK) > 0;
    }

    @Step("Check heading '{heading}' is visible on page")
    public boolean isHeadingVisible(String heading) {
        return isVisible("h1:has-text('%s'), h2:has-text('%s')".formatted(heading, heading));
    }

    @Step("Get footer text")
    public String footerText() {
        return getText(FOOTER_TEXT);
    }

    @Step("Get href of footer link '{label}'")
    public String footerLinkHref(String label) {
        return getAttribute(FOOTER_LINK.formatted(label), "href");
    }

    @Step("Get logo link href")
    public String logoHref() {
        return getAttribute(LOGO_LINK, "href");
    }

    public String currentUrl() {
        return getCurrentUrl();
    }
}
