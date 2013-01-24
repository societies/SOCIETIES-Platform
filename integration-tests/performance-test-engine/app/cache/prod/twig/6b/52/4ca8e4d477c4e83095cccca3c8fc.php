<?php

/* SecurityBundle:Collector:security.html.twig */
class __TwigTemplate_6b524ca8e4d477c4e83095cccca3c8fc extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("WebProfilerBundle:Profiler:layout.html.twig");

        $this->blocks = array(
            'toolbar' => array($this, 'block_toolbar'),
            'menu' => array($this, 'block_menu'),
            'panel' => array($this, 'block_panel'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "WebProfilerBundle:Profiler:layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_toolbar($context, array $blocks = array())
    {
        // line 4
        echo "    ";
        if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
        if ($this->getAttribute($_collector_, "user")) {
            // line 5
            echo "        ";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            $context["color_code"] = ((($this->getAttribute($_collector_, "enabled") && $this->getAttribute($_collector_, "authenticated"))) ? ("green") : ("yellow"));
            // line 6
            echo "        ";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            $context["authentication_color_code"] = ((($this->getAttribute($_collector_, "enabled") && $this->getAttribute($_collector_, "authenticated"))) ? ("green") : ("red"));
            // line 7
            echo "        ";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            $context["authentication_color_text"] = ((($this->getAttribute($_collector_, "enabled") && $this->getAttribute($_collector_, "authenticated"))) ? ("Yes") : ("No"));
            // line 8
            echo "    ";
        } else {
            // line 9
            echo "        ";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            $context["color_code"] = (($this->getAttribute($_collector_, "enabled")) ? ("red") : ("black"));
            // line 10
            echo "    ";
        }
        // line 11
        echo "    ";
        ob_start();
        // line 12
        echo "        ";
        if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
        if ($this->getAttribute($_collector_, "user")) {
            // line 13
            echo "            <div class=\"sf-toolbar-info-piece\">
                <b>Logged in as</b>
                <span class=\"sf-toolbar-status sf-toolbar-status-";
            // line 15
            if (isset($context["color_code"])) { $_color_code_ = $context["color_code"]; } else { $_color_code_ = null; }
            echo twig_escape_filter($this->env, $_color_code_, "html", null, true);
            echo "\">";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_collector_, "user"), "html", null, true);
            echo "</span>
            </div>
            <div class=\"sf-toolbar-info-piece\">
                <b>Authenticated</b>
                <span class=\"sf-toolbar-status sf-toolbar-status-";
            // line 19
            if (isset($context["authentication_color_code"])) { $_authentication_color_code_ = $context["authentication_color_code"]; } else { $_authentication_color_code_ = null; }
            echo twig_escape_filter($this->env, $_authentication_color_code_, "html", null, true);
            echo "\">";
            if (isset($context["authentication_color_text"])) { $_authentication_color_text_ = $context["authentication_color_text"]; } else { $_authentication_color_text_ = null; }
            echo twig_escape_filter($this->env, $_authentication_color_text_, "html", null, true);
            echo "</span>
            </div>
        ";
        } elseif ($this->getAttribute($_collector_, "enabled")) {
            // line 22
            echo "            You are not authenticated.
        ";
        } else {
            // line 24
            echo "            The security is disabled.
        ";
        }
        // line 26
        echo "    ";
        $context["text"] = ('' === $tmp = ob_get_clean()) ? '' : new Twig_Markup($tmp, $this->env->getCharset());
        // line 27
        echo "    ";
        ob_start();
        // line 28
        echo "        <img width=\"24\" height=\"28\" alt=\"Security\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAcCAYAAAB75n/uAAAC70lEQVR42u2V3UtTYRzHu+mFwCwK+gO6CEryPlg7yiYx50vDqUwjFIZDSYUk2ZTmCysHvg9ZVggOQZiRScsR4VwXTjEwdKZWk8o6gd5UOt0mbev7g/PAkLONIOkiBx+25/v89vuc85zn2Q5Fo9F95UDwnwhS5HK5TyqVRv8m1JN6k+AiC+fn54cwbgFNIrTQ/J9IqDcJJDGBHsgDgYBSq9W6ysvLPf39/SSUUU7zsQ1yc3MjmN90OBzfRkZG1umzQqGIxPSTkIBjgdDkaGNjoza2kcFgUCE/QvMsq6io2PV6vQu1tbV8Xl7etkql2qqvr/+MbDE/Pz8s9OP2Cjhwwmw29+4R3Kec1WZnZ4fn5uamc3Jyttra2qbH8ero6JgdHh5+CvFHq9X6JZHgzODgoCVW0NPTY0N+ltU2Nzdv4GqXsYSrPp+vDw80aLFYxru6uhyQ/rDb7a8TCVJDodB1jUazTVlxcXGQ5/mbyE+z2u7u7veY38BVT3Z2djopm5qa6isrK/tQWVn5qb29fSGR4DC4PDAwMEsZHuArjGnyGKutq6v7ajQaF6urq9/MzMz0QuSemJiwQDwGkR0POhhXgILjNTU1TaWlpTxlOp1uyWQyaUjMajMzM8Nut/tJQUHBOpZppbCwkM/KytrBznuL9xDVxBMo8KXHYnu6qKjIivmrbIy67x6Px4Yd58W672ApfzY0NCyNjo7OZmRkiAv8fr+O47iwmABXtoXaG3uykF6vX7bZbF6cgZWqqiqezYkKcNtmjO+CF2AyhufgjsvlMiU7vXEF+4C4ALf9CwdrlVAqlcFkTdRqdQSHLUDgBEeSCrArAsiGwENs0XfJBE6ncxm1D8Aj/B6tigkkJSUlmxSwLYhMDeRsyyUCd+lHrWxtbe2aTCbbZTn1ZD92F0Cr8GBfgnsgDZwDt8EzMBmHMXBLqD0PDMAh9Gql3iRIESQSIAXp4CRIBZeEjIvDFZAm1J4C6UK9ROiZcvCn/+8FvwHtDdJEaRY+oQAAAABJRU5ErkJggg==\" />
        <span class=\"sf-toolbar-status sf-toolbar-status-";
        // line 29
        if (isset($context["color_code"])) { $_color_code_ = $context["color_code"]; } else { $_color_code_ = null; }
        echo twig_escape_filter($this->env, $_color_code_, "html", null, true);
        echo "\"></span>
        ";
        // line 30
        if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
        if ($this->getAttribute($_collector_, "user")) {
            echo "<div class=\"sf-toolbar-status sf-toolbar-info-piece-additional\">";
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_collector_, "user"), "html", null, true);
            echo "</div>";
        }
        // line 31
        echo "    ";
        $context["icon"] = ('' === $tmp = ob_get_clean()) ? '' : new Twig_Markup($tmp, $this->env->getCharset());
        // line 32
        echo "    ";
        if (isset($context["profiler_url"])) { $_profiler_url_ = $context["profiler_url"]; } else { $_profiler_url_ = null; }
        $this->env->loadTemplate("WebProfilerBundle:Profiler:toolbar_item.html.twig")->display(array_merge($context, array("link" => $_profiler_url_)));
    }

    // line 35
    public function block_menu($context, array $blocks = array())
    {
        // line 36
        echo "<span class=\"label\">
    <span class=\"icon\"><img src=\"";
        // line 37
        echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("bundles/webprofiler/images/profiler/security.png"), "html", null, true);
        echo "\" alt=\"\" /></span>
    <strong>Security</strong>
</span>
";
    }

    // line 42
    public function block_panel($context, array $blocks = array())
    {
        // line 43
        echo "    <h2>Security</h2>
    ";
        // line 44
        if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
        if ($this->getAttribute($_collector_, "user")) {
            // line 45
            echo "        <table>
            <tr>
                <th>Username</th>
                <td>";
            // line 48
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_collector_, "user"), "html", null, true);
            echo "</td>
            </tr>
            <tr>
                <th>Authenticated?</th>
                <td>
                    ";
            // line 53
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            if ($this->getAttribute($_collector_, "authenticated")) {
                // line 54
                echo "                        yes
                    ";
            } else {
                // line 56
                echo "                        no ";
                if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
                if ((!twig_length_filter($this->env, $this->getAttribute($_collector_, "roles")))) {
                    echo "<em>(probably because the user has no roles)</em>";
                }
                // line 57
                echo "                    ";
            }
            // line 58
            echo "                </td>
            </tr>
            <tr>
                <th>Roles</th>
                <td>";
            // line 62
            if (isset($context["collector"])) { $_collector_ = $context["collector"]; } else { $_collector_ = null; }
            echo twig_escape_filter($this->env, $this->env->getExtension('yaml')->encode($this->getAttribute($_collector_, "roles")), "html", null, true);
            echo "</td>
            </tr>
        </table>
    ";
        } elseif ($this->getAttribute($_collector_, "enabled")) {
            // line 66
            echo "        <p>
            <em>No token</em>
        </p>
    ";
        } else {
            // line 70
            echo "        <p>
            <em>The security component is disabled</em>
        </p>
    ";
        }
    }

    public function getTemplateName()
    {
        return "SecurityBundle:Collector:security.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  201 => 70,  195 => 66,  187 => 62,  181 => 58,  178 => 57,  172 => 56,  168 => 54,  165 => 53,  156 => 48,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 37,  131 => 36,  128 => 35,  122 => 32,  119 => 31,  111 => 30,  106 => 29,  103 => 28,  100 => 27,  97 => 26,  93 => 24,  89 => 22,  79 => 19,  68 => 15,  64 => 13,  60 => 12,  57 => 11,  54 => 10,  50 => 9,  47 => 8,  43 => 7,  39 => 6,  35 => 5,  31 => 4,  28 => 3,);
    }
}
