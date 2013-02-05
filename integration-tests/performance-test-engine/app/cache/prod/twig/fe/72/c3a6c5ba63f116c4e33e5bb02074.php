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
        if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        if (($_currentPage_ == 1)) {
            // line 13
            echo "    \t\t<li class=\"disabled\"><a>Prev</a></li>
    \t\t";
        } elseif (($_currentPage_ <= $_nbrPage_)) {
            // line 15
            echo "    \t\t<li><a href=\"";
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            echo twig_escape_filter($this->env, ($_currentPage_ - 1), "html", null, true);
            echo "\">Prev</a></li>
    \t\t";
        }
        // line 17
        echo "   \t\t\t
    \t\t";
        // line 18
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable(range(1, $_nbrPage_));
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
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            if (($_item_ == $_currentPage_)) {
                // line 20
                echo "   \t \t\t\t<li class=\"disabled\"><a>";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            } elseif (($_item_ != $_currentPage_)) {
                // line 22
                echo "   \t \t\t\t<li><a href=\"";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
                echo "\">";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
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
        if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        if (($_currentPage_ == $_nbrPage_)) {
            // line 27
            echo "    \t\t<li class=\"disabled\"><a>Next</a></li>
    \t\t";
        } elseif (($_currentPage_ < $_nbrPage_)) {
            // line 29
            echo "    \t\t<li><a href=\"";
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            echo twig_escape_filter($this->env, ($_currentPage_ + 1), "html", null, true);
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
        if (isset($context["test_result_list"])) { $_test_result_list_ = $context["test_result_list"]; } else { $_test_result_list_ = null; }
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable($_test_result_list_);
        foreach ($context['_seq'] as $context["_key"] => $context["item"]) {
            // line 51
            echo "\t \t\t
\t \t\t";
            // line 52
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            if (($this->getAttribute($_item_, "status") == "success")) {
                // line 53
                echo "\t \t\t\t<tr class=\"success\">
\t \t\t";
            } elseif (($this->getAttribute($_item_, "status") == "failed")) {
                // line 55
                echo "\t \t\t\t<tr class=\"error\">
\t \t\t";
            } elseif (($this->getAttribute($_item_, "status") == "pending")) {
                // line 57
                echo "\t \t\t\t<tr class=\"warning\">
\t \t\t";
            }
            // line 59
            echo "\t \t\t
    \t\t\t<td>";
            // line 60
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($this->getAttribute($_item_, "performanceTest"), "testName"), "html", null, true);
            echo "</td>
    \t\t\t<td>";
            // line 61
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($this->getAttribute($_item_, "node"), "nodeId"), "html", null, true);
            echo "</td>
    \t\t\t<td>";
            // line 62
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_item_, "testStartDate"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 63
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_item_, "testEndDate"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 64
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_item_, "status"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 65
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_item_, "message"), "html", null, true);
            echo "</td> 
    \t\t\t<td>";
            // line 66
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            echo twig_escape_filter($this->env, $this->getAttribute($_item_, "nodeJid"), "html", null, true);
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
        if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        if (($_currentPage_ == 1)) {
            // line 87
            echo "    \t\t<li class=\"disabled\"><a>Prev</a></li>
    \t\t";
        } elseif (($_currentPage_ <= $_nbrPage_)) {
            // line 89
            echo "    \t\t<li><a href=\"";
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            echo twig_escape_filter($this->env, ($_currentPage_ - 1), "html", null, true);
            echo "\">Prev</a></li>
    \t\t";
        }
        // line 91
        echo "   \t\t\t
    \t\t";
        // line 92
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        $context['_parent'] = (array) $context;
        $context['_seq'] = twig_ensure_traversable(range(1, $_nbrPage_));
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
            if (isset($context["item"])) { $_item_ = $context["item"]; } else { $_item_ = null; }
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            if (($_item_ == $_currentPage_)) {
                // line 94
                echo "   \t \t\t\t<li class=\"disabled\"><a>";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
                echo "</a></li>
   \t \t\t\t";
            } elseif (($_item_ != $_currentPage_)) {
                // line 96
                echo "   \t \t\t\t<li><a href=\"";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
                echo "\">";
                if (isset($context["loop"])) { $_loop_ = $context["loop"]; } else { $_loop_ = null; }
                echo twig_escape_filter($this->env, $this->getAttribute($_loop_, "index"), "html", null, true);
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
        if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
        if (isset($context["nbrPage"])) { $_nbrPage_ = $context["nbrPage"]; } else { $_nbrPage_ = null; }
        if (($_currentPage_ == $_nbrPage_)) {
            // line 101
            echo "    \t\t<li class=\"disabled\"><a>Next</a></li>
    \t\t";
        } elseif (($_currentPage_ < $_nbrPage_)) {
            // line 103
            echo "    \t\t<li><a href=\"";
            if (isset($context["currentPage"])) { $_currentPage_ = $context["currentPage"]; } else { $_currentPage_ = null; }
            echo twig_escape_filter($this->env, ($_currentPage_ + 1), "html", null, true);
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
        return array (  332 => 105,  321 => 101,  317 => 100,  314 => 99,  300 => 98,  290 => 96,  283 => 94,  278 => 93,  260 => 92,  257 => 91,  250 => 89,  242 => 86,  208 => 65,  203 => 64,  193 => 62,  188 => 61,  183 => 60,  180 => 59,  176 => 57,  162 => 51,  157 => 50,  125 => 27,  104 => 24,  94 => 22,  42 => 9,  114 => 35,  98 => 32,  72 => 20,  115 => 42,  110 => 40,  96 => 8,  66 => 28,  21 => 1,  53 => 10,  51 => 13,  49 => 13,  331 => 100,  325 => 103,  322 => 95,  318 => 94,  315 => 93,  310 => 90,  304 => 86,  301 => 85,  297 => 84,  294 => 83,  289 => 80,  275 => 79,  271 => 77,  256 => 75,  246 => 87,  243 => 72,  237 => 70,  232 => 69,  224 => 66,  214 => 62,  200 => 61,  197 => 60,  190 => 58,  169 => 56,  163 => 52,  154 => 50,  136 => 31,  132 => 47,  129 => 29,  121 => 26,  113 => 41,  80 => 40,  74 => 21,  59 => 15,  52 => 14,  139 => 45,  124 => 42,  118 => 25,  109 => 34,  99 => 33,  84 => 24,  81 => 23,  73 => 20,  69 => 18,  62 => 16,  41 => 7,  123 => 24,  108 => 20,  95 => 18,  90 => 31,  87 => 20,  83 => 23,  26 => 4,  34 => 4,  102 => 34,  78 => 23,  61 => 17,  56 => 14,  38 => 6,  92 => 27,  86 => 24,  46 => 12,  37 => 5,  33 => 5,  29 => 7,  19 => 1,  44 => 12,  27 => 3,  55 => 23,  48 => 14,  45 => 11,  36 => 6,  30 => 4,  25 => 3,  248 => 96,  238 => 90,  234 => 88,  227 => 84,  223 => 69,  218 => 63,  216 => 79,  213 => 66,  210 => 77,  207 => 76,  198 => 63,  192 => 67,  177 => 61,  174 => 60,  171 => 59,  164 => 55,  160 => 51,  155 => 51,  153 => 50,  149 => 47,  146 => 47,  143 => 46,  137 => 45,  126 => 43,  116 => 22,  112 => 37,  107 => 31,  85 => 28,  82 => 19,  77 => 39,  67 => 17,  63 => 14,  32 => 7,  24 => 6,  22 => 4,  23 => 3,  20 => 2,  17 => 1,  201 => 72,  195 => 66,  187 => 62,  181 => 63,  178 => 57,  172 => 55,  168 => 53,  165 => 52,  156 => 51,  151 => 45,  148 => 44,  145 => 43,  142 => 42,  134 => 44,  131 => 44,  128 => 35,  122 => 32,  119 => 31,  111 => 21,  106 => 29,  103 => 28,  100 => 27,  97 => 28,  93 => 7,  89 => 16,  79 => 12,  68 => 30,  64 => 18,  60 => 22,  57 => 15,  54 => 15,  50 => 13,  47 => 11,  43 => 9,  39 => 9,  35 => 5,  31 => 4,  28 => 3,);
    }
}
