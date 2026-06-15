import { SOHU_PUBLISH_CONFIG } from '../utils/sohu-page.js'
import { ArticlePlatformPublisher } from '../utils/article-platform-publish.js'

export class SohuPublisher extends ArticlePlatformPublisher {
  constructor() {
    super(SOHU_PUBLISH_CONFIG)
  }
}
