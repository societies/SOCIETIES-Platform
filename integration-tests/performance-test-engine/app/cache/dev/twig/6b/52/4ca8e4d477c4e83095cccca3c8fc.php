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
        if ($this->getAttribute($this->getContext($context, "collector"), "user")) {
            // line 5
            echo "        ";
            $context["color_code"] = ((($this->getAttribute($this->getContext($context, "collector"), "enabled") && $this->getAttribute($this->getContext($context, "collector"), "authenticated"))) ? ("green") : ("yellow"));
            // line 6
            echo "        ";
            $context["authentication_color_code"] = ((($this->getAttribute($this->getContext($context, "collector"), "enabled") && $this->getAttribute($this->getContext($context, "collector"), "authenticated"))) ? ("green") : ("red"));
            // line 7
            echo "        ";
            $context["authentication_color_text"] = ((($this->getAttribute($this->getContext($context, "collector"), "enabled") && $this->getAttribute($this->getContext($context, "collector"), "authenticated"))) ? ("Yes") : ("No"));
            // line 8
            echo "    ";
        } else {
            // line 9
            echo "        ";
            $context["color_code"] = (($this->getAttribute($this->getContext($context, "collector"), "enabled")) ? ("red") : ("black"));
            // line 10
            echo "    ";
        }
        // line 11
        echo "    ";
        ob_start();
        // line 12
        echo "        ";
        if ($this->getAttribute($this->getContext($context, "collector"), "user")) {
            // line 13
            echo "            <div class=\"sf-toolbar-info-piece\">
                <b>Logged in as</b>
                <span class=\"sf-toolbar-status sf-toolbar-status-";
            // line 15
            echo twig_escape_filter($this->env, $this->getContext($context, "color_code"), "html", null, true);
            echo "\">";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "collector"), "user"), "html", null, true);
            echo "</span>
            </div>
            <div class=\"sf-toolbar-info-piece\">
                <b>Authenticated</b>
                <span class=\"sf-toolbar-status sf-toolbar-status-";
            // line 19
            echo twig_escape_filter($this->env, $this->getContext($context, "authentication_color_code"), "html", null, true);
            echo "\">";
            echo twig_escape_filter($this->env, $this->getContext($context, "authentication_color_text"), "html", null, true);
            echo "</span>
            </div>
        ";
        } elseif ($this->getAttribute($this->getContext($context, "collector"), "enabled")) {
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
        echo twig_escape_filter($this->env, $this->getContext($context, "color_code"), "html", null, true);
        echo "\"></span>
        ";
        // line 30
        if ($this->getAttribute($this->getContext($context, "collector"), "user")) {
            echo "<div class=\"sf-toolbar-status sf-toolbar-info-piece-additional\">";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "collector"), "user"), "html", null, true);
            echo "</div>";
        }
        // line 31
        echo "    ";
        $context["icon"] = ('' === $tmp = ob_get_clean()) ? '' : new Twig_Markup($tmp, $this->env->getCharset());
        // line 32
        echo "    ";
        $this->env->loadTemplate("WebProfilerBundle:Profiler:toolbar_item.html.twig")->display(array_merge($context, array("link" => $this->getContext($context, "profiler_url"))));
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
        if ($this->getAttribute($this->getContext($context, "collector"), "user")) {
            // line 45
            echo "        <table>
            <tr>
                <th>Username</th>
                <td>";
            // line 48
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "collector"), "user"), "html", null, true);
            echo "</td>
            </tr>
            <tr>
                <th>Authenticated?</th>
                <td>
                    ";
            // line 53
            if ($this->getAttribute($this->getContext($context, "collector"), "authenticated")) {
                // line 54
                echo "                        yes
                    ";
            } else {
                // line 56
                echo "                        no ";
                if ((!twig_length_filter($this->env, $this->getAttribute($this->getContext($context, "collector"), "roles")))) {
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
            echo twig_escape_filter($this->env, $this->env->getExtension('yaml')->encode($this->getAttribute($this->getContext($context, "collector"), "roles")), "html", null, true);
            echo "</td>
            </tr>
        </table>
    ";
        } elseif ($this->getAttribute($this->getContext($context, "collector"), "enabled")) {
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
        return array (  182 => 70,  176 => 66,  169 => 62,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 53,  141 => 48,  136 => 45,  134 => 44,  131 => 43,  128 => 42,  120 => 37,  117 => 36,  114 => 35,  109 => 32,  106 => 31,  100 => 30,  96 => 29,  93 => 28,  90 => 27,  87 => 26,  83 => 24,  79 => 22,  71 => 19,  62 => 15,  58 => 13,  55 => 12,  52 => 11,  49 => 10,  46 => 9,  43 => 8,  40 => 7,  37 => 6,  34 => 5,  31 => 4,  28 => 3,);
    }
}
