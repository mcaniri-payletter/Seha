package com.sbank.admin.common.domain;

import javax.validation.groups.Default;

public interface ValidationGroups {
    interface Insert extends Default {
    }

    interface Update extends Default {
    }

    interface UpdateList extends Default {
    }

    interface Delete extends Default {
    }

    interface Reset extends Default {
    }

    interface ResetMail extends Default {
    }

    interface ChangePwd extends Default {
    }
}