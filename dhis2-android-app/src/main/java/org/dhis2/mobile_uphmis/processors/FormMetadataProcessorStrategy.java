package org.dhis2.mobile_uphmis.processors;


import android.content.Context;

import org.dhis2.mobile_uphmis.io.holders.DatasetInfoHolder;
import org.dhis2.mobile_uphmis.io.json.ParsingException;
import org.dhis2.mobile_uphmis.io.models.Form;
import org.dhis2.mobile_uphmis.network.NetworkException;
import org.dhis2.mobile_uphmis.ui.adapters.dataEntry.FieldAdapter;
import org.dhis2.mobile_uphmis.utils.PrefUtils;

class FormMetadataProcessorStrategy {

    public static void process(Context context, Form form, DatasetInfoHolder info)
            throws ParsingException, NetworkException {
        String jsonContent = DataSetMetaData.download(context, info.getFormId(), PrefUtils.getServerVersion(context).equals("2.25"));
        form.setFieldCombinationRequired(
                DataElementOperandParser.isFieldCombinationRequiredToForm(jsonContent));
        DataSetMetaData.addCompulsoryDataElements(
                DataElementOperandParser.parse(jsonContent), form);

//        @Sou allow invalid feilds
        if(form.getGroups() != null && form.getGroups().size() > 0 && !form.getGroups().get(0).getLabel().equals(FieldAdapter.FORM_WITHOUT_SECTION))
            DataSetMetaData.removeFieldsWithInvalidCategoryOptionRelation(form,
                    DataSetCategoryOptionParser.parse(jsonContent));

        boolean isApproved = DataSetApprovals.download(context, info.getFormId(), info.getPeriod(), info.getOrgUnitId());
        form.setApproved(isApproved);
    }
}
