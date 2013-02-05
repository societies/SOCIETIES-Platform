<?php

/* WebProfilerBundle:Profiler:toolbar.html.twig */
class __TwigTemplate_f316b9dcc9c00aa0640217b023088c62 extends Twig_Template
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
        echo "<!-- START of Symfony2 Web Debug Toolbar -->
";
        // line 2
        if (("normal" != $this->getContext($context, "position"))) {
            // line 3
            echo "    ";
            $this->env->loadTemplate("WebProfilerBundle:Profiler:toolbar_style.html.twig")->display(array_merge($context, array("position" => $this->getContext($context, "position"), "floatable" => true)));
            // line 4
            echo "    <div style=\"clear: both; height: 38px;\"></div>
";
        }
        // line 6
        echo "
<div class=\"sf-toolbarreset\">
    ";
        // line 8
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($this->getContext($context, "templates"));
        foreach ($context['_seq'] as $context["name"] => $context["template"]) {
            // line 9
            echo "        ";
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "template"), "renderblock", array(0 => "toolbar", 1 => array("collector" => $this->getAttribute($this->getContext($context, "profile"), "getcollector", array(0 => $this->getContext($context, "name")), "method"), "profiler_url" => $this->getContext($context, "profiler_url"), "token" => $this->getAttribute($this->getContext($context, "profile"), "token"), "name" => $this->getContext($context, "name"))), "method"), "html", null, true);
            // line 15
            echo "
    ";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['name'], $context['template'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 17
        echo "
    ";
        // line 18
        if (("normal" != $this->getContext($context, "position"))) {
            // line 19
            echo "        <a class=\"hide-button\" title=\"Close Toolbar\" onclick=\"
            var p = this.parentNode;
            p.style.display = 'none';
            (p.previousElementSibling || p.previousSibling).style.display = 'none';
        \"></a>
    ";
        }
        // line 25
        echo "</div>
<!-- END of Symfony2 Web Debug Toolbar -->
";
    }

    public function getTemplateName()
    {
        return "WebProfilerBundle:Profiler:toolbar.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  18 => 1,  784 => 466,  781 => 465,  770 => 463,  766 => 462,  762 => 460,  749 => 459,  723 => 454,  720 => 453,  701 => 451,  684 => 450,  680 => 448,  676 => 447,  672 => 446,  668 => 445,  664 => 444,  660 => 443,  657 => 442,  655 => 441,  638 => 440,  627 => 439,  612 => 434,  607 => 432,  603 => 431,  600 => 430,  586 => 429,  554 => 399,  536 => 396,  519 => 395,  516 => 394,  514 => 393,  509 => 391,  504 => 389,  248 => 136,  159 => 79,  144 => 72,  135 => 69,  122 => 59,  97 => 43,  386 => 160,  383 => 159,  377 => 158,  375 => 157,  368 => 156,  364 => 155,  360 => 153,  358 => 152,  355 => 151,  352 => 150,  350 => 149,  342 => 147,  340 => 146,  337 => 145,  328 => 140,  325 => 139,  318 => 135,  312 => 131,  309 => 130,  306 => 129,  304 => 128,  278 => 114,  273 => 111,  252 => 101,  245 => 97,  238 => 93,  232 => 89,  229 => 88,  224 => 86,  219 => 83,  205 => 76,  200 => 92,  191 => 68,  188 => 67,  167 => 82,  207 => 77,  174 => 84,  168 => 60,  88 => 28,  60 => 25,  186 => 87,  172 => 58,  165 => 64,  132 => 54,  354 => 163,  345 => 160,  341 => 159,  338 => 158,  333 => 157,  331 => 141,  323 => 138,  321 => 149,  314 => 145,  307 => 141,  300 => 137,  286 => 129,  279 => 125,  272 => 121,  257 => 109,  250 => 100,  226 => 87,  215 => 83,  212 => 82,  204 => 78,  201 => 77,  190 => 72,  180 => 86,  146 => 73,  133 => 47,  126 => 45,  108 => 50,  67 => 20,  111 => 40,  138 => 42,  84 => 29,  75 => 18,  289 => 101,  284 => 99,  270 => 98,  262 => 105,  256 => 103,  253 => 93,  236 => 97,  233 => 91,  227 => 89,  221 => 86,  193 => 66,  185 => 64,  177 => 85,  173 => 61,  166 => 59,  162 => 80,  154 => 53,  124 => 31,  95 => 23,  105 => 49,  54 => 15,  21 => 3,  38 => 6,  51 => 38,  47 => 17,  299 => 125,  293 => 121,  290 => 120,  287 => 119,  285 => 118,  280 => 115,  274 => 86,  271 => 110,  268 => 84,  266 => 107,  261 => 80,  247 => 79,  243 => 96,  228 => 75,  220 => 73,  218 => 72,  213 => 79,  209 => 81,  202 => 71,  196 => 69,  183 => 68,  181 => 63,  175 => 59,  158 => 59,  107 => 42,  101 => 25,  80 => 19,  63 => 23,  36 => 7,  156 => 56,  148 => 55,  142 => 50,  140 => 71,  127 => 45,  123 => 44,  115 => 55,  110 => 40,  85 => 34,  65 => 21,  59 => 18,  45 => 8,  103 => 41,  91 => 41,  74 => 17,  70 => 22,  66 => 19,  25 => 4,  89 => 28,  82 => 27,  92 => 33,  86 => 39,  77 => 18,  57 => 13,  19 => 2,  42 => 11,  29 => 6,  26 => 3,  223 => 88,  214 => 90,  210 => 78,  203 => 93,  199 => 83,  194 => 69,  192 => 90,  189 => 70,  187 => 77,  184 => 76,  178 => 72,  170 => 64,  157 => 78,  152 => 75,  145 => 58,  130 => 47,  125 => 52,  119 => 45,  116 => 29,  112 => 26,  102 => 35,  98 => 24,  76 => 26,  73 => 24,  69 => 23,  56 => 14,  32 => 8,  24 => 9,  22 => 3,  23 => 29,  17 => 1,  68 => 13,  61 => 21,  44 => 12,  20 => 2,  161 => 63,  153 => 50,  150 => 49,  147 => 51,  143 => 57,  137 => 45,  129 => 46,  121 => 35,  118 => 50,  113 => 44,  104 => 35,  99 => 33,  94 => 21,  81 => 28,  78 => 32,  72 => 25,  64 => 28,  53 => 15,  50 => 18,  48 => 18,  41 => 11,  39 => 12,  35 => 7,  33 => 8,  30 => 4,  27 => 6,  182 => 70,  176 => 62,  169 => 83,  163 => 54,  160 => 53,  155 => 56,  151 => 62,  149 => 47,  141 => 43,  136 => 55,  134 => 50,  131 => 31,  128 => 39,  120 => 51,  117 => 45,  114 => 31,  109 => 25,  106 => 41,  100 => 34,  96 => 32,  93 => 34,  90 => 21,  87 => 28,  83 => 38,  79 => 24,  71 => 16,  62 => 11,  58 => 25,  55 => 20,  52 => 19,  49 => 37,  46 => 20,  43 => 13,  40 => 15,  37 => 9,  34 => 9,  31 => 10,  28 => 7,);
    }
}
