package org.angularjs.codeInsight.refs;

import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.lang.javascript.psi.resolve.CachingPolyReferenceBase;
import com.intellij.lang.javascript.psi.resolve.JSResolveResult;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import org.angularjs.index.AngularIndexUtil;
import org.angularjs.index.AngularUiRouterStatesIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Irina.Chernushina on 2/12/2016.
 */
public class AngularJSUiRouterStatesReferencesProvider extends PsiReferenceProvider {
  @NotNull
  @Override
  public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    return new PsiReference[] {new AngularJSUiRouterStateReference(((XmlAttributeValue)element))};
  }

  private static class AngularJSUiRouterStateReference extends CachingPolyReferenceBase<XmlAttributeValue> {
    public AngularJSUiRouterStateReference(XmlAttributeValue element) {
      super(element, ElementManipulators.getValueTextRange(element));
    }

    private String getStateName() {
      final String text = StringUtil.unquoteString(getCanonicalText());
      final int idx = text.indexOf('(');
      if (idx >= 0) return text.substring(0, idx);
      return text;
    }

    @NotNull
    @Override
    protected ResolveResult[] resolveInner() {
      final String id = getStateName();
      if (StringUtil.isEmptyOrSpaces(id)) {
        return ResolveResult.EMPTY_ARRAY;
      }

      final List<ResolveResult> list = new ArrayList<>();
      AngularIndexUtil.multiResolve(myElement.getProject(), AngularUiRouterStatesIndex.KEY, id,
                                    element -> {
                                      list.add(new JSResolveResult(element));
                                      return true;
                                    });
      return list.toArray(new ResolveResult[list.size()]);
    }

    @Override
    public boolean isSoft() {
      return true;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
  }
}
