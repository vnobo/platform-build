import {TestBed} from '@angular/core/testing';

import {PlatformLayoutService} from './platform-layout.service';

describe('PlatformLayoutService', () => {
  let service: PlatformLayoutService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlatformLayoutService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
