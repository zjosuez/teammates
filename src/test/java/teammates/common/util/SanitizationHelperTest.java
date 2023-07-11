package teammates.common.util;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link SanitizationHelper}.
 */
public class SanitizationHelperTest extends BaseTestCase {

    @Test
    public void testSanitizeGoogleId() {
        assertEquals("big-small.20_12 @Gmail.COM", SanitizationHelper.sanitizeGoogleId(" big-small.20_12 @Gmail.COM \t\n"));
        assertEquals("user@hotmail.com", SanitizationHelper.sanitizeGoogleId(" user@hotmail.com \t\n"));
    }

    @Test
    public void testSanitizeEmail() {
        String emailWithWhiteSpaces = "\tnormal@email.com \t\n";
        String normalEmail = "normal@email.com";

        assertNull(SanitizationHelper.sanitizeEmail(null));
        assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(normalEmail));
        assertEquals(normalEmail, SanitizationHelper.sanitizeEmail(emailWithWhiteSpaces));
    }

    @Test
    public void testSanitizeName() {

        String nameWithWhiteSpaces = "\t alice   bob \t\n";
        String normalName = "alice bob";

        assertNull(SanitizationHelper.sanitizeName(null));
        assertEquals(normalName, SanitizationHelper.sanitizeName(normalName));
        assertEquals(normalName, SanitizationHelper.sanitizeName(nameWithWhiteSpaces));
    }

    @Test
    public void testSanitizeTitle() {
        // tested as name
    }

    @Test
    public void testSanitizeTextField() {
        // tested as email
    }

    @Test
    public void testSanitizeForHtml() {
        sanitizeHtml_receivesNull_returnsNull();
        sanitizeHtml_receivesCodeInjection_returnsSanitized();
        sanitizeHtml_receivesSanitized_returnsUnchanged();
    }

    private void sanitizeHtml_receivesNull_returnsNull() {
        assertNull(SanitizationHelper.sanitizeForHtml((String) null));
    }

    private void sanitizeHtml_receivesCodeInjection_returnsSanitized() {
        String unsanitized = "< > \" / ' &"
                             + "<script>alert('injected');</script>";
        String expected = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                          + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        String sanitized = SanitizationHelper.sanitizeForHtml(unsanitized);
        assertEquals(expected, sanitized);
    }

    private void sanitizeHtml_receivesSanitized_returnsUnchanged() {
        String sanitized = "&lt; &gt; &quot; &#x2f; &#39; &amp;"
                           + "&lt;script&gt;alert(&#39;injected&#39;);&lt;&#x2f;script&gt;";
        assertEquals(sanitized, SanitizationHelper.sanitizeForHtml(sanitized));
    }

    @Test
    public void testSanitizeForRichText() {
        assertNull(SanitizationHelper.sanitizeForRichText((String) null));
        assertEquals("", SanitizationHelper.sanitizeForRichText(""));
        assertEquals("<p>wihtout changes</p>", SanitizationHelper.sanitizeForRichText("<p>wihtout changes</p>"));
        assertEquals("<p>spaces test</p>", SanitizationHelper.sanitizeForRichText("<p >spaces test</p >"));
        String actualRichText = "<body onload=\"alert('onload');\">"
                                + "<a href=\"https://teammatesv4.appspot.com\" onclick=\"alert('fail');\"></a>"
                                + "<script>alert('fail');</script>"
                                + "<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>"
                                + "<hr />"
                                + "<img src=\"https://teammatesv4.appspot.com/images/overview.png\" />"
                                + "<p style=\"text-align:center\"><strong>Content</strong></p>"
                                + "<div onmouseover=\"alert('onmouseover');\"></div>"
                                + "<iframe></iframe>"
                                + "<input></input>"
                                + "<span style=\"color:#339966\">Content</span>";
        String expectedRichText = "<a href=\"https://teammatesv4.appspot.com\"></a>"
                                  + "<h1></h1><h2></h2><h3></h3><h4></h4><h5></h5><h6></h6>"
                                  + "<hr />"
                                  + "<img src=\"https://teammatesv4.appspot.com/images/overview.png\" />"
                                  + "<p style=\"text-align:center\"><strong>Content</strong></p>"
                                  + "<div></div>"
                                  + "<span style=\"color:#339966\">Content</span>";
        String sanitized = SanitizationHelper.sanitizeForRichText(actualRichText);
        assertEquals(expectedRichText, sanitized);

        actualRichText = "<table cellspacing = \"5\" onmouseover=\"alert('onmouseover');\">"
                + "<thead><tr><td>No.</td><td colspan = \"2\">People</td></tr></thead>"
                + "<caption> Table with caption</caption>"
                + "<tbody><tr><td>1</td><td>Amy</td><td><strong>Smith</strong></td></tr>"
                + "<tr><td>2</td><td>Bob</td><td><strong>Tan</strong></td></tr>"
                + "</tbody></table>"
                + "<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>"
                + "</td></option></div> invalid closing tags"
                + "f(x) = x<sup>2</sup>"
                + "<code>System.out.println(\"Hello World\");</code>";
        expectedRichText = "<table cellspacing=\"5\">"
                + "<thead><tr><td>No.</td><td colspan=\"2\">People</td></tr></thead>"
                + "<caption> Table with caption</caption>"
                + "<tbody><tr><td>1</td><td>Amy</td><td><strong>Smith</strong></td></tr>"
                + "<tr><td>2</td><td>Bob</td><td><strong>Tan</strong></td></tr>"
                + "</tbody></table>"
                + "<p>Chemical formula: C<sub>6</sub>H<sub>12</sub>O<sub>6</sub></p>"
                + " invalid closing tags"
                + "f(x) &#61; x<sup>2</sup>"
                + "<code>System.out.println(&#34;Hello World&#34;);</code>";
        sanitized = SanitizationHelper.sanitizeForRichText(actualRichText);
        assertEquals(expectedRichText, sanitized);
    }

    //    @Test
//    public void populateTemplate_ReplaceStringValues_ReturnsPopulatedTemplate() {
//        String template = "Hello {{name}}, your age is {{age}}.";
//        String name = "John Doe";
//        String age = "30";
//
//        String result = populateTemplate(template, "{{name}}", name, "{{age}}", age);
//
//        String expected = "Hello John Doe, your age is 30.";
//        assertEquals(expected, result);
//    }

    @Test
    public void populateTemplate_ReplaceStringAndObjectValues_ReturnsPopulatedTemplate() {
        String template = "Hello {{name}}, your age is {{age}}.";
        String name = "John Doe";
        int age = 30;

        String result = populateTemplate4(template, "{{name}}", name, "{{age}}", age);

        String expected = "Hello John Doe, your age is 30.";
        assertEquals(expected, result);
    }

    @Test
    public void populateTemplate_InvalidKeyValuePairsLength_ThrowsAssertionError() {
        String template = "Hello {{name}}, your age is {{age}}.";
        String name = "John Doe";

        assertThrows(AssertionError.class, () -> populateTemplate(template, "{{name}}", name, "{{age}}"));
    }

    @Test
    public void populateTemplate_ReplaceWithNullValues_ReturnsPopulatedTemplateWithNulls() {
        String template = "Hello {{name}}, your age is {{age}}.";
        String name = null;
        int age = 30;

        String result = populateTemplate(template, "{{name}}", name, "{{age}}", age);

        String expected = "Hello null, your age is 30.";
        assertEquals(expected, result);
    }

    @Test
    public void populateTemplate_ReplaceWithMultipleOccurrences_ReturnsPopulatedTemplateWithAllReplacements() {
        String template = "Hello {{name}}, your name is {{name}}.";
        String name = "John Doe";

        String result = populateTemplate(template, "{{name}}", name);

        String expected = "Hello John Doe, your name is John Doe.";
        assertEquals(expected, result);
    }

    @Test
    public void populateTemplate_ReplaceWithEmptyString_ReturnsPopulatedTemplateWithEmptyString() {
        String template = "Hello {{name}}.";
        String name = "";

        String result = populateTemplate5(template, "{{name}}", name);

        String expected = "Hello .";
        assertEquals(expected, result);
    }




}
