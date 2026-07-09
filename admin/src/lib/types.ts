export type NotificationChannel = "EMAIL" | "WEBSOCKET";

export type NotificationStatus = "PENDING" | "SENT" | "DEAD";

export type DeliveryAttemptStatus = "SUCCESS" | "FAILURE";

export interface NotificationSummary {
  id: number;
  clientName: string;
  channel: NotificationChannel;
  recipient: string;
  subject: string | null;
  status: NotificationStatus;
  createdAt: string;
}

export interface DeliveryAttempt {
  attemptNumber: number;
  status: DeliveryAttemptStatus;
  errorMessage: string | null;
  attemptedAt: string;
}

export interface NotificationDetail {
  id: number;
  clientId: number;
  clientName: string;
  channel: NotificationChannel;
  recipient: string;
  subject: string | null;
  body: string;
  status: NotificationStatus;
  createdAt: string;
  updatedAt: string;
  attempts: DeliveryAttempt[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ClientSummary {
  id: number;
  name: string;
  active: boolean;
}

export interface CreateClientResult {
  id: number;
  name: string;
  apiKey: string;
}

export interface Template {
  id: number;
  code: string;
  channel: NotificationChannel;
  content: string;
  createdAt: string;
  updatedAt: string;
}
