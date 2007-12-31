<%--
    Copyright 2007 The Photogal Team.

    This file is part of photogal.

    photogal is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    photogal is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with photogal.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ tag body-content="empty" description="Writes the viewing navbar"%>
<%@ attribute name="selected" required="false"%>
<%@ taglib uri="/WEB-INF/photogal.tld" prefix="pg"%>

<div id="navbar"><pg:navlink selected="${'galleries' == selected}"
	url="/showGalleries.do">Galleries</pg:navlink>
<div class="greenText" style="float:left;">|</div>
<pg:navlink selected="${'keywords' == selected}"
	url="/showKeywordList.do">Keywords</pg:navlink>
<div class="greenText" style="float:left;">|</div>
<pg:navlink selected="${'by-date' == selected}" url="/showDateIndex.do">By Date</pg:navlink></div>
