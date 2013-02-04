<?php

/* SocietiesTestEngineBundle:TestResult:test_result.html.twig */
class __TwigTemplate_fe72c3a6c5ba63f116c4e33e5bb02074 extends Twig_Template
{
    public function __construct(Twig_Environment $env)
    {
        parent::__construct($env);

        $this->parent = $this->env->loadTemplate("SocietiesTestEngineBundle::layout.html.twig");

        $this->blocks = array(
            'title' => array($this, 'block_title'),
            'body' => array($this, 'block_body'),
        );
    }

    protected function doGetParent(array $context)
    {
        return "SocietiesTestEngineBundle::layout.html.twig";
    }

    protected function doDisplay(array $context, array $blocks = array())
    {
        $this->parent->display($context, array_merge($this->blocks, $blocks));
    }

    // line 3
    public function block_title($context, array $blocks = array())
    {
        $this->displayParentBlock("title", $context, $blocks);
    }

    // line 5
    public function block_body($context, array $blocks = array())
    {
        // line 6
        echo "\t";
        $this->displayParentBlock("body", $context, $blocks);
        echo "
\t
\t<h3>Test Result Table</h3>
\t
\t<div class=\"pagination pagination-centered\">   
    \t<ul>
    \t\t";
        // line 12
        if (($this->getContext($context, "currentPage") == 1)) {
            // line 13
            echo "    \t\t<li class=\"disabled\"><a>Prev</a></li>
    \t\t";
        } elseif (($this->getContext($context, "currentPage") <= $this->getContext($context, "nbrPage"))) {
            // line 15
            echo "    \t\t<li><a href=\"";
            echo twig_escape_filter($this->env, ($this->getContext($context, "currentPage") - 1), "html", null, true);
            echo "\">Prev</a></li>
    \t\t";
        }
        // line 17
        echo "   \t\t\t
    \t\t";
        // line 18
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable(range(1, $this->getContext($context, "nbrPage")));
        $context['loop'] = array(
          'parent' => $context['_parent'],
          'index0' => 0,
          'index'  => 1,
          'first'  => true,
        );
        if (is_array($context['_seq']) || (is_object($context['_seq']) && $context['_seq'] instanceof Countable)) {
            $length = count($context['_seq']);
            $context['loop']['revindex0'] = $length - 1;
            $context['loop']['revindex'] = $length;
            $context['loop']['length'] = $length;
            $context['loop']['last'] = 1 === $length;
        }
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            // line 19
            echo "    \t\t\t";
            if (($this->getContext($context, "item") == $this->getContext($context, "currentPage"))) {
                // line 20
                echo "   \t \t\t\t<li class=\"disabled\"><a>";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            } elseif (($this->getContext($context, "item") != $this->getContext($context, "currentPage"))) {
                // line 22
                echo "   \t \t\t\t<li><a href=\"";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "\">";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            }
            // line 24
            echo "   \t\t\t";
            ++$context['loop']['index0'];
            ++$context['loop']['index'];
            $context['loop']['first'] = false;
            if (isset($context['loop']['length'])) {
                --$context['loop']['revindex0'];
                --$context['loop']['revindex'];
                $context['loop']['last'] = 0 === $context['loop']['revindex0'];
            }
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 25
        echo "   \t\t\t
   \t\t\t";
        // line 26
        if (($this->getContext($context, "currentPage") == $this->getContext($context, "nbrPage"))) {
            // line 27
            echo "    \t\t<li class=\"disabled\"><a>Next</a></li>
    \t\t";
        } elseif (($this->getContext($context, "currentPage") < $this->getContext($context, "nbrPage"))) {
            // line 29
            echo "    \t\t<li><a href=\"";
            echo twig_escape_filter($this->env, ($this->getContext($context, "currentPage") + 1), "html", null, true);
            echo "\">Next</a></li>
    \t\t";
        }
        // line 31
        echo "    \t\t
    \t</ul>
    </div>
\t   
\t  
\t<table class=\"table table-bordered\">
\t\t<thead> 
\t\t\t<tr>
    \t\t\t<th>Test</th>
    \t\t\t<th>Sent to</th>
    \t\t\t<th>Start Date</th> 
    \t\t\t<th>End Date</th> 
    \t\t\t<th>Status</th> 
    \t\t\t<th>Message</th> 
    \t\t\t<th>Received from</th>
\t\t\t</tr> 
\t\t</thead>
\t\t<tbody>
\t
\t\t";
        // line 50
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($this->getContext($context, "test_result_list"));
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            // line 51
            echo "\t \t\t
\t \t\t";
            // line 52
            if (($this->getAttribute($this->getContext($context, "item"), "status") == "success")) {
                // line 53
                echo "\t \t\t\t<tr class=\"success\">
\t \t\t";
            } elseif (($this->getAttribute($this->getContext($context, "item"), "status") == "failed")) {
                // line 55
                echo "\t \t\t\t<tr class=\"error\">
\t \t\t";
            } elseif (($this->getAttribute($this->getContext($context, "item"), "status") == "pending")) {
                // line 57
                echo "\t \t\t\t<tr class=\"warning\">
\t \t\t";
            }
            // line 59
            echo "\t \t\t
    \t\t\t<td>";
            // line 60
            echo twig_escape_filter($this->env, $this->getAttribute($this->getAttribute($this->getContext($context, "item"), "performanceTest"), "testName"), "html", null, true);
            echo "</td>
    \t\t\t<td>";
            // line 61
            echo twig_escape_filter($this->env, $this->getAttribute($this->getAttribute($this->getContext($context, "item"), "node"), "nodeId"), "html", null, true);
            echo "</td>
    \t\t\t<td>";
            // line 62
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "testStartDate"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 63
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "testEndDate"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 64
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "status"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 65
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "message"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 66
            echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "item"), "nodeJid"), "html", null, true);
            echo "</td>
\t\t\t</tr>
\t\t";
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 69
        echo "\t\t\t
\t\t</tbody>
\t\t<tfoot>
\t\t\t<tr>
    \t\t\t<th>Test</th>
    \t\t\t<th>Sent to</th>
    \t\t\t<th>Start Date</th> 
    \t\t\t<th>End Date</th> 
    \t\t\t<th>Status</th> 
    \t\t\t<th>Message</th> 
    \t\t\t<th>Received from</th>
\t\t\t</tr>
\t\t</tfoot>
   \t</table>
   \t
   \t<div class=\"pagination pagination-centered\">   
    \t<ul>
    \t\t";
        // line 86
        if (($this->getContext($context, "currentPage") == 1)) {
            // line 87
            echo "    \t\t<li class=\"disabled\"><a>Prev</a></li>
    \t\t";
        } elseif (($this->getContext($context, "currentPage") <= $this->getContext($context, "nbrPage"))) {
            // line 89
            echo "    \t\t<li><a href=\"";
            echo twig_escape_filter($this->env, ($this->getContext($context, "currentPage") - 1), "html", null, true);
            echo "\">Prev</a></li>
    \t\t";
        }
        // line 91
        echo "   \t\t\t
    \t\t";
        // line 92
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable(range(1, $this->getContext($context, "nbrPage")));
        $context['loop'] = array(
          'parent' => $context['_parent'],
          'index0' => 0,
          'index'  => 1,
          'first'  => true,
        );
        if (is_array($context['_seq']) || (is_object($context['_seq']) && $context['_seq'] instanceof Countable)) {
            $length = count($context['_seq']);
            $context['loop']['revindex0'] = $length - 1;
            $context['loop']['revindex'] = $length;
            $context['loop']['length'] = $length;
            $context['loop']['last'] = 1 === $length;
        }
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            // line 93
            echo "   \t \t\t\t";
            if (($this->getContext($context, "item") == $this->getContext($context, "currentPage"))) {
                // line 94
                echo "   \t \t\t\t<li class=\"disabled\"><a>";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            } elseif (($this->getContext($context, "item") != $this->getContext($context, "currentPage"))) {
                // line 96
                echo "   \t \t\t\t<li><a href=\"";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "\">";
                echo twig_escape_filter($this->env, $this->getAttribute($this->getContext($context, "loop"), "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            }
            // line 98
            echo "   \t\t\t";
            ++$context['loop']['index0'];
            ++$context['loop']['index'];
            $context['loop']['first'] = false;
            if (isset($context['loop']['length'])) {
                --$context['loop']['revindex0'];
                --$context['loop']['revindex'];
                $context['loop']['last'] = 0 === $context['loop']['revindex0'];
            }
        }
        $_parent = $context['_parent'];
        unset($context['_seq'], $context['_iterated'], $context['_key'], $context['item'], $context['_parent'], $context['loop']);
        $context = array_merge($_parent, array_intersect_key($context, $_parent));
        // line 99
        echo "   \t\t\t
   \t\t\t";
        // line 100
        if (($this->getContext($context, "currentPage") == $this->getContext($context, "nbrPage"))) {
            // line 101
            echo "    \t\t<li class=\"disabled\"><a>Next</a></li>
    \t\t";
        } elseif (($this->getContext($context, "currentPage") < $this->getContext($context, "nbrPage"))) {
            // line 103
            echo "    \t\t<li><a href=\"";
            echo twig_escape_filter($this->env, ($this->getContext($context, "currentPage") + 1), "html", null, true);
            echo "\">Next</a></li>
    \t\t";
        }
        // line 105
        echo "    \t</ul>
    </div>
\t
\t

";
    }

    public function getTemplateName()
    {
        return "SocietiesTestEngineBundle:TestResult:test_result.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  289 => 101,  284 => 99,  270 => 98,  262 => 96,  256 => 94,  253 => 93,  236 => 92,  233 => 91,  227 => 89,  221 => 86,  193 => 66,  185 => 64,  177 => 62,  173 => 61,  166 => 59,  162 => 57,  154 => 53,  124 => 31,  95 => 24,  105 => 35,  54 => 14,  21 => 1,  38 => 6,  51 => 22,  47 => 11,  299 => 105,  293 => 103,  290 => 95,  287 => 100,  285 => 93,  280 => 90,  274 => 86,  271 => 85,  268 => 84,  266 => 83,  261 => 80,  247 => 79,  243 => 77,  228 => 75,  220 => 73,  218 => 72,  213 => 70,  209 => 69,  202 => 69,  196 => 63,  183 => 61,  181 => 63,  175 => 58,  158 => 55,  107 => 41,  101 => 34,  80 => 24,  63 => 16,  36 => 6,  156 => 58,  148 => 55,  142 => 50,  140 => 50,  127 => 45,  123 => 44,  115 => 42,  110 => 40,  85 => 28,  65 => 19,  59 => 24,  45 => 11,  103 => 28,  91 => 20,  74 => 22,  70 => 14,  66 => 18,  25 => 4,  89 => 20,  82 => 45,  92 => 32,  86 => 31,  77 => 23,  57 => 15,  19 => 2,  42 => 9,  29 => 5,  26 => 3,  223 => 87,  214 => 90,  210 => 88,  203 => 84,  199 => 83,  194 => 80,  192 => 62,  189 => 65,  187 => 77,  184 => 76,  178 => 72,  170 => 67,  157 => 61,  152 => 52,  145 => 50,  130 => 48,  125 => 49,  119 => 45,  116 => 44,  112 => 26,  102 => 36,  98 => 33,  76 => 24,  73 => 23,  69 => 20,  56 => 12,  32 => 7,  24 => 3,  22 => 3,  23 => 3,  17 => 1,  68 => 30,  61 => 18,  44 => 7,  20 => 2,  161 => 63,  153 => 50,  150 => 49,  147 => 51,  143 => 46,  137 => 45,  129 => 42,  121 => 47,  118 => 29,  113 => 41,  104 => 35,  99 => 33,  94 => 21,  81 => 20,  78 => 19,  72 => 16,  64 => 15,  53 => 10,  50 => 13,  48 => 13,  41 => 7,  39 => 7,  35 => 5,  33 => 5,  30 => 4,  27 => 3,  182 => 70,  176 => 71,  169 => 60,  163 => 58,  160 => 57,  155 => 56,  151 => 54,  149 => 51,  141 => 54,  136 => 47,  134 => 50,  131 => 43,  128 => 47,  120 => 37,  117 => 36,  114 => 27,  109 => 25,  106 => 29,  100 => 30,  96 => 8,  93 => 7,  90 => 28,  87 => 22,  83 => 24,  79 => 29,  71 => 21,  62 => 17,  58 => 17,  55 => 23,  52 => 15,  49 => 14,  46 => 12,  43 => 9,  40 => 7,  37 => 5,  34 => 4,  31 => 4,  28 => 3,);
    }
}
