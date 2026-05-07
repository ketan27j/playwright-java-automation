package com.framework.steps;

import com.framework.pages.CommentHookPage;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommentHookSteps {

    private final CommentHookPage page = new CommentHookPage();

    // ── Background ────────────────────────────────────────────────────────────

    @Given("the user opens the CommentHook homepage")
    public void openHomepage() {
        page.open();
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    @When("the user clicks the {string} nav link")
    public void clickNavLink(String label) {
        page.clickNavLink(label);
    }

    @When("the user navigates to {string}")
    public void navigateToPath(String path) {
        page.openPath(path);
    }

    // ── Page title ────────────────────────────────────────────────────────────

    @Then("the page title should be {string}")
    public void pageTitleEquals(String expected) {
        assertEquals(expected, page.title(), "page title");
    }

    @Then("the page title should contain {string}")
    public void pageTitleContains(String expected) {
        assertTrue(page.title().contains(expected),
                "Expected page title to contain '%s' but got '%s'".formatted(expected, page.title()));
    }

    // ── Hero section ──────────────────────────────────────────────────────────

    @Then("the hero heading should contain {string}")
    public void heroHeadingContains(String expected) {
        assertTrue(page.heroHeading().contains(expected),
                "Expected hero heading to contain '%s' but got '%s'".formatted(expected, page.heroHeading()));
    }

    @Then("the hero description should mention {string}")
    public void heroDescriptionMentions(String keyword) {
        assertTrue(page.heroDescriptionContains(keyword),
                "Expected hero description to mention '%s'".formatted(keyword));
    }

    // ── Nav bar ───────────────────────────────────────────────────────────────

    @Then("the nav bar should have a {string} link pointing to {string}")
    public void navLinkPointsTo(String label, String expectedHref) {
        assertEquals(expectedHref, page.navLinkHref(label),
                "nav '%s' href".formatted(label));
    }

    // ── CTA buttons ───────────────────────────────────────────────────────────

    @Then("the {string} CTA should link to {string}")
    public void ctaLinksTo(String label, String expectedHref) {
        assertEquals(expectedHref, page.ctaHref(label),
                "CTA '%s' href".formatted(label));
    }

    // ── How-it-works section ──────────────────────────────────────────────────

    @Then("the section heading {string} should be visible")
    public void sectionHeadingVisible(String heading) {
        assertTrue(page.isSectionHeadingVisible(heading),
                "Expected section heading '%s' to be visible".formatted(heading));
    }

    @Then("the step {string} should be visible")
    public void stepVisible(String step) {
        assertTrue(page.isStepVisible(step),
                "Expected step '%s' to be visible".formatted(step));
    }

    // ── Features section ──────────────────────────────────────────────────────

    @Then("the feature {string} should be visible")
    public void featureVisible(String feature) {
        assertTrue(page.isFeatureVisible(feature),
                "Expected feature '%s' to be visible".formatted(feature));
    }

    // ── Pricing section ───────────────────────────────────────────────────────

    @Then("the pricing plan {string} should display price containing {string}")
    public void pricingPlanHasPrice(String plan, String expectedPrice) {
        assertTrue(page.isPlanVisible(plan),
                "Expected pricing plan '%s' to be visible".formatted(plan));
        assertTrue(page.planPrice(plan).contains(expectedPrice),
                "Expected plan '%s' price to contain '%s'".formatted(plan, expectedPrice));
    }

    @Then("the {string} pricing plan should be labelled {string}")
    public void planHasLabel(String plan, String label) {
        assertTrue(page.isPlanMostPopular(plan),
                "Expected plan '%s' to be labelled '%s'".formatted(plan, label));
    }

    @Then("all {string} buttons in the pricing section should link to {string}")
    public void allPricingButtonsLinkTo(String buttonLabel, String expectedHref) {
        assertTrue(page.allGetStartedLinksPointTo(expectedHref),
                "Expected all '%s' buttons to link to '%s'".formatted(buttonLabel, expectedHref));
    }

    // ── Blog page ─────────────────────────────────────────────────────────────

    @Then("the blog page should display at least one article")
    public void blogHasArticles() {
        assertTrue(page.hasBlogArticles(), "Expected blog page to have at least one article");
    }

    // ── Content headings ──────────────────────────────────────────────────────

    @Then("the heading {string} should be visible")
    public void headingVisible(String heading) {
        assertTrue(page.isHeadingVisible(heading),
                "Expected heading '%s' to be visible".formatted(heading));
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    @Then("the footer copyright should contain {string}")
    public void footerCopyrightContains(String expected) {
        assertTrue(page.footerText().contains(expected),
                "Expected footer to contain '%s'".formatted(expected));
    }

    @Then("the footer should have a {string} link pointing to {string}")
    public void footerLinkPointsTo(String label, String expectedHref) {
        assertEquals(expectedHref, page.footerLinkHref(label),
                "footer '%s' href".formatted(label));
    }

    // ── Logo ──────────────────────────────────────────────────────────────────

    @Then("the logo link href should be {string}")
    public void logoLinkHref(String expectedHref) {
        assertEquals(expectedHref, page.logoHref(), "logo link href");
    }
}
