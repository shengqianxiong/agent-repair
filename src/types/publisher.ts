import type { Page } from 'playwright';

export interface PublishContent {
  title: string;
  content: string;
}

export interface PublishResult {
  success: boolean;
  url?: string;
  message?: string;
}

export interface Publisher {
  platform: string;
  publish(page: Page, content: PublishContent): Promise<PublishResult>;
}
