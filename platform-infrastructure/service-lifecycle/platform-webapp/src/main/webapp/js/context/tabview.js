/* Copyright (C) 2005 Ilya S. Lyubinskiy. All rights reserved.
Technical support: http://www.php-development.ru/

YOU MAY NOT
(1) Remove or modify this copyright notice.
(2) Distribute this code, any part or any modified version of it.
    Instead, you can link to the homepage of this code:
    http://www.php-development.ru/javascripts/tabview.php.

YOU MAY
(1) Use this code on your website.
(2) Use this code as a part of another product.

NO WARRANTY
This code is provided "as is" without warranty of any kind, either
expressed or implied, including, but not limited to, the implied warranties
of merchantability and fitness for a particular purpose. You expressly
acknowledge and agree that use of this code is at your own risk.


If you find my script useful, you can support my site in the following ways:
1. Vote for the script at HotScripts.com (you can do it on my site)
2. Link to the homepage of this script or to the homepage of my site:
   http://www.php-development.ru/javascripts/tabview.php
   http://www.php-development.ru/
   You will get 50% commission on all orders made by your referrals.
   More information can be found here:
   http://www.php-development.ru/affiliates.php
*/

// ----- Auxiliary -------------------------------------------------------------

function tabview_aux(TabViewId, id)
{
  var TabView = document.getElementById(TabViewId);

  // ----- Tabs -----

  var Tabs = TabView.firstChild;
  while (Tabs.className != "Tabs" ) Tabs = Tabs.nextSibling;

  var Tab = Tabs.firstChild;
  var i   = 0;

  do
  {
    if (Tab.tagName == "A")
    {
      i++;
      Tab.href      = "javascript:tabview_switch('"+TabViewId+"', "+i+");";
      Tab.className = (i == id) ? "Active" : "";
      Tab.blur();
    }
  }
  while (Tab = Tab.nextSibling);

  // ----- Pages -----

  var Pages = TabView.firstChild;
  while (Pages.className != 'Pages') Pages = Pages.nextSibling;

  var Page = Pages.firstChild;
  var i    = 0;

  do
  {
    if (Page.className == 'Page')
    {
      i++;
      if (Pages.offsetHeight) Page.style.height = (Pages.offsetHeight-2)+"px";
      Page.style.overflow = "auto";
      Page.style.display  = (i == id) ? 'block' : 'none';
    }
  }
  while (Page = Page.nextSibling);
}

// ----- Functions -------------------------------------------------------------

function tabview_switch(TabViewId, id) { tabview_aux(TabViewId, id); }

function tabview_initialize(TabViewId) { tabview_aux(TabViewId,  1); }
