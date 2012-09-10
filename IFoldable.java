/*---

    iGeo - http://igeo.jp

    Copyright (c) 2002-2012 Satoru Sugihara

    This file is part of iGeo.

    iGeo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as
    published by the Free Software Foundation, version 3.

    iGeo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with iGeo.  If not, see <http://www.gnu.org/licenses/>.

---*/

package igeo;

/**
   Abstract interface of foldable/unfoldable geometry.
   
   @author Satoru Sugihara
*/
public interface IFoldable{

    /** unfold at origin on XY plane */
    public IFoldable unfold();
    /** unfold at origin on the given plane */
    public IFoldable unfold(IVecI planeNormal);
    /** unfold at the plane point on the given plane */
    public IFoldable unfold(IVecI planeNormal, IVecI planePt);
    
}