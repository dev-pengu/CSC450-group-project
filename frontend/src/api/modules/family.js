import { http } from "./clients";

export default {
  joinFamily(formData) {
    return http.get(
      'family/invite/join',
      {
        params: {
          code: formData.inviteCode,
          eventColor: formData.personalEventColor.replace('#', ''),
        },
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  createFamily(formData) {
    return http.post(
      'family',
      {
        name: formData.familyName,
        eventColor: formData.familyEventColor.replace('#', ''),
        timezone: formData.timezone,
        owner: {
          eventColor: formData.personalEventColor.replace('#', ''),
        },
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  async getFamilies() {
    return http.get('family/get-family');
  },
  generateInviteCode(id) {
    return http.post(
      'family/admin/invites/generate',
      {
        familyId: id,
        persistent: true,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  sendInviteCode(formData) {
    return http.post(
      'family/admin/invites/generate',
      {
        familyId: formData.family,
        persistent: false,
        recipientEmail: formData.recipientEmail,
        initialRole: formData.role,
      },
      {
        validateStatus(status) {
          return status < 500;
        },
      }
    );
  },
  getFamiliesForSelect() {
    return http.get('family/familySelect');
  },
  getMembersForSelect(familyId) {
    return http.get('family/memberSelect', { params: { id: familyId } });
  },
  transferFamilyOwnership(req) {
    return http.patch('/family/admin/transferOwnership', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  leaveFamily(id) {
    return http.delete('/family/leave', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteFamily(id) {
    return http.delete('/family/admin/delete', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateFamily(req) {
    return http.patch('/family/admin/update', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateFamilyRoles(req) {
    return http.post('/family/admin/roles', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
}
