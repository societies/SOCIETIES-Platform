<?php

/* TwigBundle:Exception:trace.html.twig */
class __TwigTemplate_663230178352dc2486fb0df8b51e6848 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = array(
        );
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        // line 1
        if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
        if ($this->getAttribute($_trace_, "function")) {
            // line 2
            echo "    at
    <strong>
        <abbr title=\"";
            // line 4
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_trace_, "class"), "html", null, true);
            echo "\">";
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_trace_, "short_class"), "html", null, true);
            echo "</abbr>
        ";
            // line 5
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo twig_escape_filter($this->env, ($this->getAttribute($_trace_, "type") . $this->getAttribute($_trace_, "function")), "html", null, true);
            echo "
    </strong>
    (";
            // line 7
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo $this->env->getExtension('code')->formatArgs($this->getAttribute($_trace_, "args"));
            echo ")
";
        }
        // line 9
        echo "
";
        // line 10
        if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
        if (((($this->getAttribute($_trace_, "file", array(), "any", true, true) && $this->getAttribute($_trace_, "file")) && $this->getAttribute($_trace_, "line", array(), "any", true, true)) && $this->getAttribute($_trace_, "line"))) {
            // line 11
            echo "    ";
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo (($this->getAttribute($_trace_, "function")) ? ("<br />") : (""));
            echo "
    in ";
            // line 12
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo $this->env->getExtension('code')->formatFile($this->getAttribute($_trace_, "file"), $this->getAttribute($_trace_, "line"));
            echo "&nbsp;
    ";
            // line 13
            ob_start();
            // line 14
            echo "    <a href=\"#\" onclick=\"toggle('trace_";
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "'); switchIcons('icon_";
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "_open', 'icon_";
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "_close'); return false;\">
        <img class=\"toggle\" id=\"icon_";
            // line 15
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "_close\" alt=\"-\" src=\"";
            echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("bundles/framework/images/blue_picto_less.gif"), "html", null, true);
            echo "\" style=\"visibility: ";
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo (((0 == $_i_)) ? ("display") : ("hidden"));
            echo "\" />
        <img class=\"toggle\" id=\"icon_";
            // line 16
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "_open\" alt=\"+\" src=\"";
            echo twig_escape_filter($this->env, $this->env->getExtension('assets')->getAssetUrl("bundles/framework/images/blue_picto_more.gif"), "html", null, true);
            echo "\" style=\"visibility: ";
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo (((0 == $_i_)) ? ("hidden") : ("display"));
            echo "; margin-left: -18px\" />
    </a>
    ";
            echo trim(preg_replace('/>\s+</', '><', ob_get_clean()));
            // line 19
            echo "    <div id=\"trace_";
            if (isset($context["prefix"])) { $_prefix_ = $context["prefix"]; } else { $_prefix_ = null; }
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo twig_escape_filter($this->env, (($_prefix_ . "_") . $_i_), "html", null, true);
            echo "\" style=\"display: ";
            if (isset($context["i"])) { $_i_ = $context["i"]; } else { $_i_ = null; }
            echo (((0 == $_i_)) ? ("block") : ("none"));
            echo "\" class=\"trace\">
        ";
            // line 20
            if (isset($context["trace"])) { $_trace_ = $context["trace"]; } else { $_trace_ = null; }
            echo $this->env->getExtension('code')->fileExcerpt($this->getAttribute($_trace_, "file"), $this->getAttribute($_trace_, "line"));
            echo "
    </div>
";
        }
    }

    public function getTemplateName()
    {
        return "TwigBundle:Exception:trace.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  102 => 19,  78 => 15,  61 => 13,  56 => 12,  38 => 7,  92 => 39,  86 => 6,  46 => 14,  37 => 8,  33 => 7,  29 => 6,  19 => 1,  44 => 9,  27 => 3,  55 => 9,  48 => 7,  45 => 6,  36 => 5,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 83,  218 => 80,  216 => 79,  213 => 78,  210 => 77,  207 => 76,  198 => 71,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 54,  155 => 51,  153 => 50,  149 => 48,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 36,  112 => 20,  107 => 31,  85 => 28,  82 => 27,  77 => 39,  67 => 24,  63 => 14,  32 => 5,  24 => 4,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 56,  168 => 54,  165 => 53,  156 => 48,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 37,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 30,  106 => 29,  103 => 28,  100 => 27,  97 => 26,  93 => 24,  89 => 16,  79 => 40,  68 => 15,  64 => 13,  60 => 22,  57 => 22,  54 => 10,  50 => 11,  47 => 10,  43 => 7,  39 => 15,  35 => 4,  31 => 4,  28 => 3,);
    }
}
