@commenthook
Feature: CommentHook Marketing Website

  Background:
    Given the user opens the CommentHook homepage

  @smoke @parallel
  Scenario: Homepage has correct page title
    Then the page title should be "Turn Instagram Comments into Customers | CommentHook"

  @smoke @parallel
  Scenario: Hero section displays value proposition
    Then the hero heading should contain "Turn every comment"
    And the hero description should mention "qualifies leads"

  @smoke @parallel
  Scenario: Navigation bar contains Pricing and Blog links
    Then the nav bar should have a "Pricing" link pointing to "/#pricing"
    And the nav bar should have a "Blog" link pointing to "/blog"

  @smoke @parallel
  Scenario: Primary CTA buttons link to the register page
    Then the "Start free — no credit card" CTA should link to "https://app.commenthook.com/register"
    And the "Start for free" CTA should link to "https://app.commenthook.com/register"

  @regression @parallel
  Scenario: How-it-works section shows all 3 setup steps
    Then the section heading "Set up in 3 minutes" should be visible
    And the step "Connect Instagram" should be visible
    And the step "Set a keyword" should be visible
    And the step "AI sends the DM" should be visible

  @regression @parallel
  Scenario: Features section highlights all 6 benefits
    Then the section heading "Why creators choose CommentHook" should be visible
    And the feature "AI-written DMs" should be visible
    And the feature "Follow-gate" should be visible
    And the feature "Responds in seconds" should be visible
    And the feature "Simple dashboard" should be visible
    And the feature "INR pricing" should be visible
    And the feature "Official Meta API" should be visible

  @regression @parallel
  Scenario Outline: Pricing section shows each plan with its correct price
    Then the pricing plan "<plan>" should display price containing "<price>"

    Examples:
      | plan     | price        |
      | Free     | ₹0           |
      | Starter  | ₹499/month   |
      | Pro      | ₹999/month   |
      | Business | ₹2,499/month |

  @regression @parallel
  Scenario: Pro plan is marked as most popular
    Then the "Pro" pricing plan should be labelled "Most popular"

  @regression @parallel
  Scenario: All pricing plan Get Started buttons link to register
    Then all "Get started" buttons in the pricing section should link to "https://app.commenthook.com/register"

  @smoke @parallel
  Scenario: Blog page loads with the correct title and a featured article
    When the user clicks the "Blog" nav link
    Then the page title should contain "Blog"
    And the blog page should display at least one article

  @regression @parallel
  Scenario: Privacy Policy page loads correctly
    When the user navigates to "/privacy"
    Then the page title should contain "Privacy Policy"
    And the heading "Privacy Policy" should be visible

  @regression @parallel
  Scenario: Terms of Service page loads correctly
    When the user navigates to "/terms"
    Then the page title should contain "Terms of Service"
    And the heading "Terms of Service" should be visible

  @regression @parallel
  Scenario: Footer shows copyright text and legal links
    Then the footer copyright should contain "CommentHook"
    And the footer should have a "Privacy" link pointing to "/privacy"
    And the footer should have a "Terms" link pointing to "/terms"

  @regression @parallel
  Scenario: CommentHook logo is a link to the homepage root
    Then the logo link href should be "/"
